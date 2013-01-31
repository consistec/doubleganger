package de.consistec.syncframework.common.conflict;

/*
 * #%L
 * Project - doppelganger
 * File - DefaultConflictStrategy.java
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

import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_MODIFIED;
import static de.consistec.syncframework.common.MdTableDefaultValues.FLAG_PROCESSED;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ConflictHandlingData;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.util.HashCalculator;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import org.slf4j.cal10n.LocLogger;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 12.12.12 13:29
 */
public class DefaultConflictStrategy implements IConflictStrategy {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(DefaultConflictStrategy.class.getCanonicalName());

    @Override
    public void resolveByClientWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException {
        int remotePK = ((Integer) data.getRemoteEntry().getPrimaryKey()).intValue();
        int localRevision = data.getLocalEntry().getRevision();
        boolean localMod = data.getLocalEntry().dataRowExists();
        String localMdv = data.getLocalEntry().getMdv();
        String remoteTableName = data.getRemoteEntry().getTableName();
        int remoteRevision = data.getRemoteEntry().getRevision();

        if (ConflictType.CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client ADD, Server ADD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_MODIFIED, remotePK, localMdv, remoteTableName);
        } else if (ConflictType.CLIENT_ADD_SERVER_DEL.isTheCase(data)) {
            logConflictInfo("Client ADD, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_MODIFIED, remotePK, localMdv, remoteTableName);
        } else if (ConflictType.CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client MOD, Server MOD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_MODIFIED, remotePK, localMdv, remoteTableName);
        } else if (ConflictType.CLIENT_MOD_SERVER_DEL.isTheCase(data)) {
            logConflictInfo("Client MOD, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_MODIFIED, remotePK, localMdv, remoteTableName);
        } else if (ConflictType.CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client DEL, Server ADD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_MODIFIED, remotePK, localMdv, remoteTableName);
        } else if (ConflictType.CLIENT_DEL_SERVER_DEL.isTheCase(data)) {
            logConflictInfo("Client DEL, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, null, remoteTableName);
        }
    }

    @Override
    public void resolveByServerWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException, NoSuchAlgorithmException {

        int remotePK = ((Integer) data.getRemoteEntry().getPrimaryKey()).intValue();
        int localRevision = data.getLocalEntry().getRevision();
        boolean localMod = data.getLocalEntry().dataRowExists();
        String remoteTableName = data.getRemoteEntry().getTableName();
        int remoteRevision = data.getRemoteEntry().getRevision();

        if (ConflictType.CLIENT_ADD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client ADD, Server ADD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateDataRow(data.getRemoteChange().getRowData(), remotePK, remoteTableName);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, data.getRemoteChange().calculateHash(),
                remoteTableName);
        } else if (ConflictType.CLIENT_ADD_SERVER_DEL.isTheCase(data)) {
            logConflictInfo(" Client ADD, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, null, remoteTableName);
            adapter.deleteRow(remotePK, remoteTableName);
        } else if (ConflictType.CLIENT_MOD_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client MOD, Server ADD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, data.getRemoteChange().calculateHash(),
                remoteTableName);
            adapter.updateDataRow(data.getRemoteChange().getRowData(), remotePK, remoteTableName);
        } else if (ConflictType.CLIENT_MOD_SERVER_DEL.isTheCase(data)) {
            logConflictInfo(" Client MOD, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, null, remoteTableName);
            adapter.deleteRow(remotePK, remoteTableName);
        } else if (ConflictType.CLIENT_DEL_SERVER_ADD_OR_SERVER_MOD.isTheCase(data)) {
            logConflictInfo("Client DEL, Server ADD", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, data.getRemoteChange().calculateHash(),
                remoteTableName);
            adapter.insertDataRow(data.getRemoteChange().getRowData(), remoteTableName);
        } else if (ConflictType.CLIENT_DEL_SERVER_DEL.isTheCase(data)) {
            logConflictInfo("Client DEL, Server DEL", data.getRemoteChange(), localRevision, localMod);
            adapter.updateMdRow(remoteRevision, FLAG_PROCESSED, remotePK, null, remoteTableName);
        }
    }

    @Override
    public void resolveByFireEvent(final IDatabaseAdapter adapter, final ConflictHandlingData data,
                                   final Map<String, Object> clientData, final IConflictListener conflictListener
    ) throws
        SyncException, DatabaseAdapterException, NoSuchAlgorithmException {

        Map<String, Object> resolved = conflictListener.resolve(data.getRemoteChange().getRowData(), clientData);
        if (rowHasData(clientData)) {

            if (!rowHasData(resolved)) {
                adapter.deleteRow(data.getRemoteEntry().getPrimaryKey(), data.getRemoteEntry().getTableName());
                adapter.updateMdRow(data.getRemoteEntry().getRevision(), FLAG_MODIFIED, data.getRemoteEntry().getPrimaryKey(),
                    null,
                    data.getRemoteEntry().getTableName());
            } else {
                adapter.updateDataRow(resolved, data.getRemoteEntry().getPrimaryKey(),
                    data.getRemoteEntry().getTableName());
                adapter.updateMdRow(data.getRemoteEntry().getRevision(), FLAG_MODIFIED, data.getRemoteEntry().getPrimaryKey(),
                    new HashCalculator().getHash(resolved), data.getRemoteEntry().getTableName());
            }
        } else {
            if (rowHasData(resolved)) {
                adapter.insertDataRow(resolved, data.getRemoteEntry().getTableName());
                adapter.updateMdRow(data.getRemoteEntry().getRevision(), FLAG_MODIFIED, data.getRemoteEntry().getPrimaryKey(),
                    new HashCalculator().getHash(resolved), data.getRemoteEntry().getTableName());

            } else {
                adapter.updateMdRow(data.getRemoteEntry().getRevision(), FLAG_PROCESSED, data.getRemoteEntry().getPrimaryKey(),
                    null,
                    data.getRemoteEntry().getTableName());
            }
        }
    }

    private boolean rowHasData(Map<String, Object> clientData) {
        if (clientData.size() > 0) {
            for (Object obj : clientData.values()) {
                if (obj != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void logConflictInfo(final String conflict, final Change remoteChange, final int localRev,
                                 final boolean exists
    ) {

        StringBuilder builder = new StringBuilder("\n/*---------------------  Conflict info   ---------------------");
        builder.append("\n * Conflict: ");
        builder.append(conflict);
        builder.append("\n * Server Change: ");
        builder.append(remoteChange);
        builder.append("\n * Client localRev: ");
        builder.append(localRev);
        builder.append(", exists: ");
        builder.append(exists);
        builder.append("\n * ---------------------  Conflict info   -------------------*/\n");

        LOGGER.debug(builder.toString());
    }

}
