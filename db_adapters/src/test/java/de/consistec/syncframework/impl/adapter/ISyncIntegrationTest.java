package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * This interface defines test cases to be implemented for database adapters integration test class.
 *
 * @author Markus
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.04.12 10:41
 * @since 0.0.1-SNAPSHOT
 */
public interface ISyncIntegrationTest {

    /**
     * @return Connection to client database. These connection is used only by the test class to prepare the data.
     * This is not the connection which is used by framework.
     */
    Connection getClientConnection();

    /**
     * @return Connection to server database. These connection is used only by the test class to prepare the data.
     * This is not the connection which is used by framework.
     */
    Connection getServerConnection();

    /**
     * If this method returns <i>null</i>, synchronization provider will be using its own connection,
     * based on frameworks configuration.
     *
     * @return External data source to be used by frameworks client synchronization provider
     * to initialize database adapter.
     * @throws Exception
     */
    DataSource getClientDataSource() throws Exception;

    /**
     * If this method returns <i>null</i>, synchronization provider will be using its own connection,
     * based on frameworks configuration.
     *
     * @return External data source to be used by frameworks server synchronization provider
     * to initialize database adapter.
     * @throws Exception
     */
    DataSource getServerDataSource() throws Exception;

    /**
     * @param resourceName
     * @return
     * @throws SyncException
     */
    InputStream getResourceAsStream(String resourceName) throws SyncException;

    void resetClientAndServerDatabase() throws SyncException, SQLException, ContextException ;
}
