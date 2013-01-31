package de.consistec.syncframework.common.conflict;

import de.consistec.syncframework.common.IConflictListener;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.client.ConflictHandlingData;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.12.12 11:56
 * @todo write comment
 */
public interface IConflictStrategy {

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @throws DatabaseAdapterException
     * @todo write comment
     */
    void resolveByClientWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException;

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @throws DatabaseAdapterException
     * @throws NoSuchAlgorithmException
     * @todo write comment
     */
    void resolveByServerWinsStrategy(final IDatabaseAdapter adapter, final ConflictHandlingData data) throws
        DatabaseAdapterException, NoSuchAlgorithmException;

    /**
     * @param adapter - database adapter to call db operations
     * @param data - data used for conflict handling
     * @param clientData - data values of conflicted data row from client of type {@code Map<String, Object>}
     * where the keys the column names from the data row are and the values
     * the content of the data row are.
     * @param conflictListener - listener to call events if configured
     * @throws SyncException
     * @throws DatabaseAdapterException
     * @throws NoSuchAlgorithmException
     * @todo write comment
     */
    void resolveByFireEvent(final IDatabaseAdapter adapter, final ConflictHandlingData data,
                            final Map<String, Object> clientData, final IConflictListener conflictListener
    ) throws SyncException, DatabaseAdapterException, NoSuchAlgorithmException;

}
