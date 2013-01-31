package de.consistec.syncframework.common.adapter;

import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.sql.SQLException;

/**
 * Callback interface for ADatabaseAdapter class.
 * <p/>
 *
 * @author Markus Backes
 * @company consistec Engineering and Consulting GmbH
 * @date 03.07.12 11:36
 * @since 0.0.1-SNAPSHOT
 */
public interface DatabaseAdapterCallback<T> {

    /**
     * The callback method.
     * <p/>
     *
     * @param result The callback result
     * @throws DatabaseAdapterException
     */
    void onSuccess(T result) throws DatabaseAdapterException, SQLException;
}
