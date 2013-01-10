package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.common.exception.SyncException;

import java.io.InputStream;
import java.sql.Connection;
import javax.sql.DataSource;

/**
 * This interface defines test cases to be implemented for database adapters integration test class.
 *
 * @author Markus
 * @company Consistec Engineering and Consulting GmbH
 * @date 20.04.12 10:41
 * @since 0.0.1-SNAPSHOT
 */
public interface ISyncTests {

    /**
     * @return Connection to client database. These connection is used only by test class to prepare the data.
     *         This is not the connection which is used by framework.
     */
    Connection getClientConnection();

    /**
     * @return Connection to server database. These connection is used only by test class to prepare the data.
     *         This is not the connection which is used by framework.
     */
    Connection getServerConnection();

    /**
     * If this method returns <i>null</i>, synchronization provider will be using its own connection,
     * based on frameworks configuration.
     *
     * @return External data source to be used by frameworks client synchronization provider
     *         to initialize database adapter.
     * @throws Exception
     */
    DataSource getClientDataSource() throws Exception;

    /**
     * If this method returns <i>null</i>, synchronization provider will be using its own connection,
     * based on frameworks configuration.
     *
     * @return External data source to be used by frameworks server synchronization provider
     *         to initialize database adapter.
     * @throws Exception
     */
    DataSource getServerDataSource() throws Exception;

    /**
     * @param resourceName
     * @return
     * @throws SyncException
     */
    InputStream getResourceAsStream(String resourceName) throws SyncException;

//    /**
//     * Test client unchanged - server unchanged case.
//     * @throws Exception
//     */
//    void testUcUc() throws Exception;
//    /**
//     * Test client added - server unchanged case.
//     * @throws Exception
//     */
//    void testAddUc() throws Exception;
//    /**
//     * Test client modified - server unchanged case.
//     * @throws Exception
//     */
//    void testModUc() throws Exception;
//    /**
//     * Test client deleted - server unchanged case.
//     * @throws Exception
//     */
//    void testDelUc() throws Exception;
//    /**
//     * Test client unchanged - server added case.
//     * @throws Exception
//     */
//    void testUcAdd() throws Exception;
//    /**
//     * Test client added - server added case.
//     * @throws Exception
//     */
//    void testAddAdd() throws Exception;
//    /**
//     * Test client mod - server added case.
//     * @throws Exception
//     */
//    void testModAdd() throws Exception;
//    /**
//     * Test client deleted - server added case.
//     * @throws Exception
//     */
//    void testDelAdd() throws Exception;
//    /**
//     * Test client unchanged - server modified case.
//     * @throws Exception
//     */
//    void testUcMod() throws Exception;
//    /**
//     * Test client added - server modified case.
//     * @throws Exception
//     */
//    void testAddMod() throws Exception;
//    /**
//     * Test client modified - server modified case.
//     * @throws Exception
//     */
//    void testModMod() throws Exception;
//    /**
//     * Test client deleted - server modified case.
//     * @throws Exception
//     */
//    void testDelMod() throws Exception;
//    /**
//     * Test client unchanged - server deleted case.
//     * @throws Exception
//     */
//    void testUcDel() throws Exception;
//    /**
//     * Test client added - server deleted case.
//     * @throws Exception
//     */
//    void testAddDel() throws Exception;
//    /**
//     * Test client modified - server modified case.
//     * @throws Exception
//     */
//    void testModDel() throws Exception;
//    /**
//     * Test client deleted - server deleted case.
//     * @throws Exception
//     */
//    void testDelDel() throws Exception;
}
