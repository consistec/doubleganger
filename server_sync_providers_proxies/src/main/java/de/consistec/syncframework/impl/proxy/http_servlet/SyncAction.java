package de.consistec.syncframework.impl.proxy.http_servlet;

/**
 * Available actions to invoke on synchronization Servlet.
 * <p/>
 * @company Consistec Engineering and Consulting GmbH
 * @date 02.11.2012 11:10:39
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public enum SyncAction {

    /**
     * Corresponds with {@link de.consistec.syncframework.common.server.IServerSyncProvider#getSchema() } method of
     * server provider.
     */
    GET_SCHEMA("getschema"),
    /**
     * Corresponds with {@link de.consistec.syncframework.common.server.IServerSyncProvider#getChanges(int) } method of
     * server provider.
     */
    GET_CHANGES("getchanges"),
    /**
     * Corresponds with
     * {@link de.consistec.syncframework.common.server.IServerSyncProvider#applyChanges(java.util.List, int) }
     * method of
     * server provider.
     */
    APPLY_CHANGES("applychanges");

    private String name;

    private SyncAction(String stringName) {
        name = stringName;
    }

    /**
     * Return the name of action as it should be written into the request.
     *
     * @return Action name is it is written in request header.
     */
    public String getStringName() {
        return name;
    }

    /**
     * Produces SyncAction instances.
     * <p/>
     * @param stringName Action name as it is used in http request.
     * @return Instance of SyncAction corresponding to given <i>stringName</i>
     */
    public static SyncAction fromStringName(String stringName) {
        for (SyncAction val : SyncAction.values()) {
            if (val.getStringName().equalsIgnoreCase(stringName)) {
                return val;
            }
        }
        return null;
    }
}
