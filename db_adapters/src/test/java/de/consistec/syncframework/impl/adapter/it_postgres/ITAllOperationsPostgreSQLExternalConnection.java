package de.consistec.syncframework.impl.adapter.it_postgres;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.Ignore;

/**
 * Performs integration test with
 * {@link de.consistec.syncframework.impl.adapter.PostgresDatabaseAdapter PostgresDatabaseAdapter}
 * and PostgreSQL database.
 * <br/>This class performs test with external connections (non adapater-managed).
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 05.11.2012 17:18:34
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class ITAllOperationsPostgreSQLExternalConnection extends ITAllOperationsPostgreSQL {

//<editor-fold defaultstate="expanded" desc=" Class fields " >
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class constructors " >
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class methods " >
    @AfterClass
    public static void tearDown() throws SQLException {

        clientConnection.close();
        serverConnection.close();

        for (Connection conn : clientDs.getCreatedConnections()) {
            assertTrue("Client connection is not closed", conn.isClosed());
        }

        for (Connection conn : serverDs.getCreatedConnections()) {
            assertTrue("Server connection is not closed", conn.isClosed());
        }

    }

    @Override
    public DataSource getClientDataSource() {
        return clientDs;
    }

    @Override
    public DataSource getServerDataSource() {
        return serverDs;
    }

//</editor-fold>
}
