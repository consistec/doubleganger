package de.consistec.syncframework.common;

/**
 * Container for sync data values. This container contains the client and server datas.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 23.01.13 08:38
 */
public class SyncDataHolder {

    private SyncData clientSyncData;
    private SyncData serverSyncData;


    /**
     * Constructor for this container.
     *
     * @param clientSyncData data values from client
     * @param serverSyncData data values from server
     */
    public SyncDataHolder(final SyncData clientSyncData, final SyncData serverSyncData) {
        this.clientSyncData = clientSyncData;
        this.serverSyncData = serverSyncData;
    }

    /**
     * Returns the data values from client.
     *
     * @return client data values
     */
    public SyncData getClientSyncData() {
        return clientSyncData;
    }

    /**
     * Returns the data values from server.
     *
     * @return server data values
     */
    public SyncData getServerSyncData() {
        return serverSyncData;
    }
}
