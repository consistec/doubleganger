package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.impl.adapter.DumpDataSource.SupportedDatabases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @company Consistec Engineering and Consulting GmbH
 * @date 11.01.2013 11:43:45
 * @author davidm
 * @since
 */
public class TestDatabase {

    private final SupportedDatabases supportedDb;
    private final String configFile;
    private final String[] createQueries;
    private DumpDataSource serverDs, clientDs;
    private Connection serverConnection, clientConnection;

    public TestDatabase(String configFile, DumpDataSource.SupportedDatabases supportedDb, String[] createQueries) {
        this.configFile = configFile;
        this.supportedDb = supportedDb;
        this.createQueries = createQueries;
    }

    public void init() throws SQLException {
        serverDs = new DumpDataSource(supportedDb, ConnectionType.SERVER);
        clientDs = new DumpDataSource(supportedDb, ConnectionType.CLIENT);
        serverConnection = serverDs.getConnection();
        clientConnection = clientDs.getConnection();
    }

    public void clean() throws SQLException {
        for (Connection connection : serverDs.getCreatedConnections()) {
            connection.close();
        }
        for (Connection connection : clientDs.getCreatedConnections()) {
            connection.close();
        }
    }

    public String getConfigFile() {
        return configFile;
    }

    public SupportedDatabases getSupportedDb() {
        return supportedDb;
    }

    public String[] getCreateQueries() {
        return createQueries;
    }

    public DumpDataSource getServerDs() {
        return serverDs;
    }

    public DumpDataSource getClientDs() {
        return clientDs;
    }

    public int executeUpdateOnServer(String query) throws SQLException {
        return executeQueriesOnServer(new String[]{query})[0];
    }

    public int executeUpdateOnClient(String query) throws SQLException {
        return executeQueriesOnClient(new String[]{query})[0];
    }

    public int[] executeQueriesOnServer(String[] queries) throws SQLException {
        return executeQueries(ConnectionType.SERVER, queries);
    }

    public int[] executeQueriesOnClient(String[] queries) throws SQLException {
        return executeQueries(ConnectionType.CLIENT, queries);
    }

    public int[] dropTablesOnServer(String[] tables) throws SQLException {
        return dropTables(ConnectionType.SERVER, tables);
    }

    public int[] dropTablesOnClient(String[] tables) throws SQLException {
        return dropTables(ConnectionType.CLIENT, tables);
    }

    public void createTablesOnServer() throws SQLException {
        executeQueries(ConnectionType.SERVER, createQueries);
    }

    public void createTablesOnClient() throws SQLException {
        executeQueries(ConnectionType.CLIENT, createQueries);
    }

    private int[] dropTables(final ConnectionType type, String[] tables) throws SQLException {
        String[] queries = new String[tables.length];
        for (int i = 0; i < tables.length; i++) {
            queries[i] = String.format("drop table if exists %s", tables[i]);
        }
        return executeQueries(type, queries);
    }

    private int[] executeQueries(final ConnectionType type, String[] queries) throws SQLException {
        final Connection connection = getConnectionFromType(type);
        final Statement statement = connection.createStatement();
        for (String query : queries) {
            statement.addBatch(query);
        }
        return statement.executeBatch();
    }

    private Connection getConnectionFromType(ConnectionType type) {
        switch (type) {
            case CLIENT:
                return clientConnection;
            case SERVER:
                return serverConnection;
            default:
                throw new IllegalArgumentException("Unknown connection type: " + ConnectionType.class
                    .getSimpleName());
        }
    }
}
