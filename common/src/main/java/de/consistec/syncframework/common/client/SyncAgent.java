package de.consistec.syncframework.common.client;

/*
 * #%L
 * Project - doppelganger
 * File - SyncAgent.java
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

import static de.consistec.syncframework.common.i18n.MessageReader.read;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashSet;
import static de.consistec.syncframework.common.util.Preconditions.checkNotNull;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.ISyncProgressListener;
import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDataHolder;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.exception.ServerStatusException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.Infos;
import de.consistec.syncframework.common.i18n.Warnings;
import de.consistec.syncframework.common.server.IServerSyncProvider;
import de.consistec.syncframework.common.server.ServerStatus;
import de.consistec.syncframework.common.util.LoggingUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.slf4j.cal10n.LocLogger;

/**
 * The SyncAgent class coordinates synchronization process on client site.
 * <p>
 * Do not use directly! Instead, use {@link de.consistec.syncframework.common.SyncContext.client()}
 * factory methods to obtain client context and performs client site operations.
 * </p>
 * <p>
 * This class is used by the {@link de.consistec.syncframework.common.SyncContext.ClientContext} to carry out
 * synchronization procedure.
 * </p>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date unknown
 * @since 0.0.1-SNAPSHOT
 */
public class SyncAgent {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(SyncAgent.class.getCanonicalName());
    private IServerSyncProvider serverProvider;
    private IClientSyncProvider clientProvider;
    private Set<ISyncProgressListener> listeners = newHashSet();
    private int syncRetries;
    private boolean isSyncAgain;
    private int recursionDepth;

    private long phaseTime;

    /**
     * Instantiates a new sync agent.
     * Before objects will be initialized, a check will be performed, if client context was initialized.
     * If not, an {@link IllegalStateException} will be thrown.
     *
     * @param serverProvider Server sync provider.
     * @param clientProvider Client sync provider.
     */
    public SyncAgent(IServerSyncProvider serverProvider, IClientSyncProvider clientProvider) {

        checkNotNull(serverProvider, read(Errors.COMMON_SERVER_PROVIDER_NOT_INITIALIZED));
        checkNotNull(clientProvider, read(Errors.COMMON_CLIENT_PROVIDER_NOT_INITIALIZED));
        this.serverProvider = serverProvider;
        this.clientProvider = clientProvider;
        this.syncRetries = Config.getInstance().getSyncRetryNumber();
    }

    /**
     * Adds progressListener to listener collection.
     * <p/>
     *
     * @param listener Progress listener
     */
    public void addProgressListener(ISyncProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the progress listener.
     *
     * @param listener the listener
     */
    public void removeProgressListener(ISyncProgressListener listener) {
        listeners.remove(listener);
    }

    /**
     * Sets the conflict listener.
     * This method allows the developer to set the conflict listener on client synchronization provider
     * through agent object.
     * <p/>
     *
     * @param listener the new conflict listener
     * @see IClientSyncProvider#setConflictListener(de.consistec.syncframework.common.IConflictListener)
     */
    public void setConflictListener(IConflictListener listener) {
        clientProvider.setConflictListener(listener);
    }

    /**
     * Gets the conflict listener.
     * This method allows the developer to obtain the conflict listener from client provider, through the agent object.
     * <p/>
     *
     * @return the conflict listener
     * @see IClientSyncProvider#getConflictListener()
     */
    public IConflictListener getConflictListener() {
        return clientProvider.getConflictListener();
    }

    /**
     * Update synchronization progress.
     * It invokes on all progress listeners
     * {@link de.consistec.syncframework.common.ISyncProgressListener#progressUpdate(java.lang.String) }
     * method an pass the {@code message} on.
     *
     * @param message the message
     */
    private void updateProgress(String message) {
        ISyncProgressListener listener;
        Iterator<ISyncProgressListener> i = listeners.iterator();
        while (i.hasNext()) {
            listener = i.next();
            listener.progressUpdate(message);
        }
    }

    /**
     * Announce end of synchronization procedure.
     * It invokes on all progress listeners
     * {@link de.consistec.syncframework.common.ISyncProgressListener#syncFinished() } method.
     */
    private void updateFinished() {
        ISyncProgressListener listener;
        Iterator<ISyncProgressListener> i = listeners.iterator();
        while (i.hasNext()) {
            listener = i.next();
            listener.syncFinished();
        }
    }

    /**
     * Default synchronization procedure for client side.
     *
     * @throws SyncException When synchronization fails.
     */
    public void synchronize() throws SyncException {

        if (isSyncAgain) {
            LOGGER.info(Infos.COMMON_SYNCHRONIZING_AGAIN);
        }

        try {
            int clientRevision = clientProvider.getLastRevision();

            // transaction phase 1 server
            doBeforeGetServerChanges();
            SyncData serverData = serverProvider.getChanges(clientRevision);
            doAfterGetServerChanges();

            // transaction phase 1 client
            clientProvider.beginTransaction();
            SyncData clientData = clientProvider.getChanges();
            SyncDataHolder dataHolder = clientProvider.resolveConflicts(serverData, clientData);
            SyncData clientChangesToApply = dataHolder.getClientSyncData();
            int currentRevision = clientProvider.applyChanges(dataHolder.getServerSyncData());
            clientProvider.commit();
            clientChangesToApply.setRevision(currentRevision);

            // transaction phase 2 server
            doBeforeApplyClientChanges();
            int serverRevision = serverProvider.applyChanges(clientChangesToApply);
            doAfterApplyClientChanges();

            clientChangesToApply.setRevision(serverRevision);

            // transaction phase 2 client
            clientProvider.updateClientRevision(clientChangesToApply);

            updateProgress(read(Infos.COMMON_SYNCHRONIZATION_FINISHED));

        } catch (ServerStatusException ex) {
            LOGGER.warn(Warnings.COMMON_CLIENT_CAUGHT_SERVER_STATUS_EXCEPTION, ex.getStatus().name(), ex.getMessage());

            if ((ex.getStatus() == ServerStatus.CLIENT_NOT_UPTODATE || ex.getStatus() == ServerStatus.ENTRY_NOT_UNIQUE)
                && syncRetries > 0) {

                LOGGER.info(Infos.COMMON_NUMBER_OF_SYNC_RETRIES, syncRetries);
                isSyncAgain = true;
                syncRetries--;
                LOGGER.info(Infos.COMMON_REMAINING_NUMBER_OF_SYNC_RETRIES, syncRetries);
                recursionDepth++;
                synchronize();

            } else {
                throw ex;
            }
        } catch (DatabaseAdapterInstantiationException e) {
            throw new SyncException(e);
        } catch (SyncException e) {
            LOGGER.warn(Warnings.COMMON_CLIENT_CAUGHT_SERVER_STATUS_EXCEPTION, e, e.getMessage());

            if (e.getCause() instanceof UniqueConstraintException && syncRetries > 0) {

                LOGGER.info(Infos.COMMON_NUMBER_OF_SYNC_RETRIES, syncRetries);
                isSyncAgain = true;
                syncRetries--;
                LOGGER.info(Infos.COMMON_REMAINING_NUMBER_OF_SYNC_RETRIES, syncRetries);
                recursionDepth++;
                synchronize();

            } else {
                throw e;
            }
        } catch (DatabaseAdapterException e) {
            throw new SyncException(e);
        } finally {
            if (recursionDepth > 0) {
                recursionDepth--;
                LOGGER.info(Infos.COMMON_SYNC_RETRY_RECOGNIZED);
            }
        }
    }

    /**
     * @todo write comment
     */
    protected void doBeforeGetServerChanges() {
        LOGGER.info(Infos.COMMON_REQUESTING_CHANGES_FROM_SERVER);
        updateProgress(read(Infos.COMMON_REQUESTING_CHANGES_FROM_SERVER));

        phaseTime = System.currentTimeMillis();
    }

    /**
     * @todo write comment.
     */
    protected void doAfterGetServerChanges() {
        phaseTime = System.currentTimeMillis() - phaseTime;
        LOGGER.debug("phase process -server-changes duration: {}ms", phaseTime);
        updateProgress(read(Infos.COMMON_REQUESTING_CHANGES_FROM_SERVER_FINISHED));
    }

    /**
     * @todo write comment.
     */
    protected void doBeforeApplyClientChanges() {
        LOGGER.info(Infos.COMMON_REQUESTING_CHANGES_FROM_CLIENT);
        phaseTime = System.currentTimeMillis();
        updateProgress(read(Infos.COMMON_APPLYING_CHANGES_FROM_CLIENT));
    }

    /**
     * @todo write comment.
     */
    protected void doAfterApplyClientChanges() {
        phaseTime = System.currentTimeMillis() - phaseTime;
        LOGGER.debug("phase process-client-changes duration: {}ms", phaseTime);
        updateProgress(read(Infos.COMMON_APPLYING_CHANGES_FROM_CLIENT_FINISHED));
    }

    private void logInfo(int clientRevision, List<Change> serverChanges) {

        StringBuilder builder = new StringBuilder("\n\tClient sends revision: ");
        builder.append(clientRevision);
        builder.append("\n\tChangeset from server:");
        builder.append("\n\t--------------------------------------------------------");
        if (serverChanges == null) {
            builder.append("\n\tNo Server changes found!");
        } else {
            for (Change change : serverChanges) {
                builder.append("\n\t");
                builder.append(change.toString());
            }

            builder.append("\n\t<");
            builder.append(serverChanges);
            builder.append(">");
        }
        builder.append("\n\t--------------------------------------------------------\n");
        LOGGER.debug(builder.toString());
    }

    /**
     * This string representation is a subject of change.
     * But it will look something like that
     * {@code SyncAgent{ serverProvider=ClassName, clientProvider=className, nrOfListeners=nr ...}}.
     *
     * @return String representation of instance state.
     */
    @Override
    public String toString() {
        String nullString = "null";
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("{ serverProvider=");
        builder.append(serverProvider == null ? nullString : serverProvider.getClass().getCanonicalName());
        builder.append(",\n clientProvider=");
        builder.append(clientProvider == null ? nullString : clientProvider.getClass().getCanonicalName());
        builder.append(",\n nrOfListeners=");
        builder.append(listeners == null ? nullString : listeners.size());
        builder.append(",\n syncRetries=");
        builder.append(syncRetries);
        builder.append(",\n isSyncAgain=");
        builder.append(isSyncAgain);
        builder.append(",\n recursionDepth=");
        builder.append(recursionDepth);
        builder.append(" }\n");
        return builder.toString();
    }
}
