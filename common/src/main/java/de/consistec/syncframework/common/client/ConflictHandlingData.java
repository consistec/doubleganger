package de.consistec.syncframework.common.client;

import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.MDEntry;

import java.util.Map;

/**
 * The class {@code ConflictHandlingData} contains client data values of any row and the row
 * from the server changeset with the same primary key which are necessary for the conflict handling.
 *
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.12.12 13:39
 */
public class ConflictHandlingData {

    private int localRev;
    private int localFlag;
    private String localMdv;
    private Change remoteChange;
    private Change clientChange;


    /**
     * Constructor of the class {@code ConflictHandlingData}.
     *
     * @param localRev - revision of any data row from client
     * @param localFlag - flag of any data row from client
     * @param localMdv - hash value  of any data row from client
     * @param remoteChange - server changeset from server with the same primary key as the client data row
     */
    public ConflictHandlingData(int localRev, int localFlag, String localMdv, Change remoteChange
    ) {
        this.localRev = localRev;
        this.localFlag = localFlag;
        this.localMdv = localMdv;
        this.remoteChange = remoteChange;
    }

    /**
     * Constructor of the class {@code ConflictHandlingData}.
     *
     * @param clientChange - client change from client with the same primary key as the server data row
     * @param serverChange - server change from server with the same primary key as the client data row
     */
    public ConflictHandlingData(Change clientChange, Change serverChange
    ) {
        this.clientChange = clientChange;
        this.remoteChange = serverChange;
    }

    /**
     * returns the remote entry of the data row from the server
     * with the same primary key as the client data row has.
     *
     * @return remote entry from server
     */
    public MDEntry getRemoteEntry() {
        return remoteChange.getMdEntry();
    }

    /**
     * returns the change of type {@code Change} from the server changeset
     * with the same primary key as the client data row has.
     *
     * @return the change from server changeset
     */
    public Change getRemoteChange() {
        return remoteChange;
    }

    /**
     * returns the remote entry of the data row from the server
     * with the same primary key as the client data row has.
     *
     * @return remote entry from server
     */
    public MDEntry getLocalEntry() {
        return clientChange.getMdEntry();
    }

    /**
     * returns the client data values as map where the key is the column name and the value the data value is.
     *
     * @return data values of client
     */
    public Map<String, Object> getLocalData() {
        return clientChange.getRowData();
    }
}
