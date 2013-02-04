package de.consistec.syncframework.common.server;

/*
 * #%L
 * Project - doppelganger
 * File - ServerHashProcessor.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_PROCESSED;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_DELETED_VALUE;
import static de.consistec.syncframework.common.MdTableDefaultValues.MDV_MODIFIED_VALUE;
import static de.consistec.syncframework.common.i18n.MessageReader.read;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * The class {@code ServerHashProcessor} is responsible for applying the changes from client
 * on the server.
 * On the basis of the passed client revision the server determines if the client is up to date.
 * If the client is not up to date then a {@code ServerStatusException} is thrown so that the
 * client can retry the synchronization. Then the {@code ServerHashProcessor} looks for each client change
 * in the server's meta and data table and does the following operations depends on the result:
 * <br/>
 * <ul>
 * <li>If no entry related to client change exists in meta table => CLIENT-ADD</li>
 * <li>If an entry exists in meta table:</li>
 * <ul>
 * <li>if client change is marked as deleted => CLIENT-DEL,</li>
 * <li>if client change is not marked as deleted => CLIENT-MOD,</li>
 * <li>if the revision of client change is 0 => illegal state (throws IllegalStateException) and</li>
 * <li>if the revision of client change is not equal the revision of server's md entry =>
 * illegal state (throws IllegalStateException)</li>
 * </ul>
 * </ul>
 * <p/>
 * <br/>
 * Which operation the {@code ServerHashProcessor} will proceed depends on the above listed the states.
 * <p/>
 * <table>
 * <tr><th>State</th><th>Operation</th></tr>
 * <tr><td>CLIENT-ADD</td><td>insertMDRow and insertDataRow</td></tr>
 * <tr><td>CLIENT-DEL</td><td>deleteDataRow and updateMDRow</td></tr>
 * <tr><td>CLIENT-MOD</td><td>insertDataRow or updateDataRow and updateMDRow</td></tr>
 * <tr><td>ADD-ADD-Conflict (remote entry revision equals 0)</td>
 * <td>That shouldn't happen! There will be thrown an IllegalStateException.</td></tr>
 * <tr><td>OUT-OF-DATE (remote entry revision not equal server entry revision)</td>
 * <td>That shouldn't happen! There will be thrown an IllegalStateException.</td></tr>
 * </table>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class ServerHashProcessor {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final LocLogger LOGGER = LoggingUtil.createLogger(ServerHashProcessor.class.getCanonicalName());
    private static final Config CONF = Config.getInstance();
    private IDatabaseAdapter adapter;

    //</editor-fold>
    /**
     * Instantiates a new server hash processor.
     *
     * @param adapter Database adapter.
     */
    public ServerHashProcessor(IDatabaseAdapter adapter) {
        this.adapter = adapter;
        LOGGER.debug("HashProcessor Constructor finished");
    }

    /**
     * Apply changes from client on server.
     *
     * @param clientChanges Client changes.
     * @param clientRevision Client revision.
     * @return New revision.
     * @throws DatabaseAdapterException the adapter exception
     * @throws ServerStatusException
     */
    public int applyChangesFromClientOnServer(List<Change> clientChanges, int clientRevision) throws
        DatabaseAdapterException, ServerStatusException {

        LOGGER.debug("applyChangesFromClientOnServer called");

        final int nextRev = adapter.getNextRevision();

        LOGGER.info(Infos.COMMON_NEW_SERVER_REVISION, nextRev);
        LOGGER.debug("compare client revision with current server revision {} : {}", clientRevision, (nextRev - 1));

        if (clientRevision != (nextRev - 1)) {
            LOGGER.warn(Warnings.COMMON_CANT_APLY_CLIENT_CHANGES_ON_SERVER);
            throw new ServerStatusException(ServerStatus.CLIENT_NOT_UPTODATE, read(Errors.COMMON_UPDATE_NECESSARY));
        }

        for (final Change remoteChange : clientChanges) {

            final MDEntry remoteEntry = remoteChange.getMdEntry();
            final Map<String, Object> remoteRowData = remoteChange.getRowData();
            final Object primaryKey = remoteEntry.getPrimaryKey();
            final String tableName = remoteEntry.getTableName();
            final String mdTableName = tableName + CONF.getMdTableSuffix();
            final String hash;
            if (CONF.isSqlTriggerActivated()) {
                hash = MDV_MODIFIED_VALUE;
            } else {
                try {
                    hash = remoteChange.calculateHash();
                } catch (NoSuchAlgorithmException ex) {
                    throw new DatabaseAdapterException(ex);
                }
            }

            LOGGER.debug("processing: {}", remoteEntry.toString());

            adapter.getRowForPrimaryKey(primaryKey, mdTableName,
                new DatabaseAdapterCallback<ResultSet>() {
                    @Override
                    public void onSuccess(final ResultSet hashRst) throws DatabaseAdapterException {
                        adapter.getRowForPrimaryKey(primaryKey, tableName,
                            new DatabaseAdapterCallback<ResultSet>() {
                                @Override
                                public void onSuccess(final ResultSet dataRst) throws DatabaseAdapterException {
                                    LOGGER.debug("call processResultSets ...");
                                    try {
                                        processResultSets(hashRst, dataRst, nextRev, hash, remoteEntry, remoteRowData);
                                    } catch (SQLException e) {
                                        throw new DatabaseAdapterException(e);
                                    }
                                }
                            });
                    }
                });

        }
        LOGGER.debug("applyChangesFromClientOnServer called");
        return nextRev;
    }

    private void processResultSets(ResultSet hashRst, ResultSet data, int nextRev, String hash, MDEntry remoteEntry,
        Map<String, Object> remoteRowData) throws SQLException, DatabaseAdapterException {

        LOGGER.debug("processResultSets called");

        final Object pKey = remoteEntry.getPrimaryKey();
        final String tableName = remoteEntry.getTableName();

        if (hashRst.next()) {
            if (!remoteEntry.dataRowExists()) {

                // CLIENT DEL
                LOGGER.info(Infos.COMMON_CLIENT_DELETED_CASE_DETECTED);
                adapter.deleteRow(pKey, tableName);
                adapter.updateMdRow(nextRev, FLAG_PROCESSED, pKey, MDV_DELETED_VALUE, tableName);

            } else {

                // CLIENT MOD
                LOGGER.info(Infos.COMMON_CLIENT_MODIFIED_CASE_DETECTED);

                if (!data.next()) {
                    adapter.insertDataRow(remoteRowData, tableName);
                } else {
                    adapter.updateDataRow(remoteRowData, pKey, tableName);
                }
                adapter.updateMdRow(nextRev, FLAG_PROCESSED, pKey, hash, tableName);
            }
        } else {

            // CLIENT ADD
            LOGGER.info(Infos.COMMON_CLIENT_ADDED_CASE_DETECTED);
            LOGGER.debug("insert md row with rev: {} and client pk: {}", nextRev, pKey);

            adapter.insertMdRow(nextRev, FLAG_PROCESSED, pKey, hash, tableName);
            adapter.insertDataRow(remoteRowData, tableName);
        }
    }
}
