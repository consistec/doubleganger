package de.consistec.syncframework.impl.adapter;

import de.consistec.syncframework.impl.ResultSetHelper;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.exception.SyncException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

/**
 * Contains methods to facilitate test data preparation.
 *
 * @author Marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.10.12 16:41
 * @since 0.0.1-SNAPSHOT
 */
public class ExecuteStatementHelper {

    private ResultSet clientResultSet, serverResultSet;
    private Connection clientConnection, serverConnection;
    private Map<String, String> clientTableContentMap = newHashMap(), serverTableContentMap = newHashMap();
//    private Map<String, String> serverTableContentMap = newHashMap();

    /**
     * Initialize object with database connections.
     *
     * @param clientConnection Connection to client database
     * @param serverConnection Connection to server database
     */
    public ExecuteStatementHelper(Connection serverConnection, Connection clientConnection) {
        this.clientConnection = clientConnection;
        this.serverConnection = serverConnection;
    }

    public int executeUpdateOnClient(String query) throws SQLException {
        return executeQueriesOnClient(new String[]{query})[0];
    }

    public int[] dropTablesOnClient(String[] tables) throws SQLException {
        return dropTables(ConnectionType.CLIENT, tables);
    }

    public int[] executeQueriesOnClient(String[] queries) throws SQLException {
        return executeQueries(ConnectionType.CLIENT, queries);
    }

    public int executeUpdateOnServer(String query) throws SQLException {
        return executeQueriesOnServer(new String[]{query})[0];
    }

    public int[] executeQueriesOnServer(String[] queries) throws SQLException {
        return executeQueries(ConnectionType.SERVER, queries);
    }

    public int[] dropTablesOnServer(String[] tables) throws SQLException {
        return dropTables(ConnectionType.SERVER, tables);
    }

    private int[] dropTables(final ConnectionType type, String[] tables) throws SQLException {
        final Connection connection = getConnectionFromType(type);
        final Statement statement = connection.createStatement();
        for (String table : tables) {
            statement.addBatch(String.format("drop table if exists %s", table));
        }
        return statement.executeBatch();
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

    /**
     * Executes provided SQL statements and compares theirs result.
     *
     * @param statementsToExecute SQL statements to execute.
     * @param comparator Comparator object for ResultSets.
     * @throws SQLException
     * @throws SyncException
     */
    public void executeStatementAndCompareResults(Map<String, String> statementsToExecute, Config conf,
        ConnectionType type,
        ConnectionType type2) throws SQLException, SyncException {

        for (String tableName : statementsToExecute.keySet()) {
            Statement clientStmt = null;
            Statement serverStmt = null;
            Statement contentRelatedToStrategyStmt = null;
            String statement = statementsToExecute.get(tableName);

            if (tableName.endsWith(conf.getMdTableSuffix())) {
                continue;
            }

            try {
                clientStmt = clientConnection.createStatement();
                clientResultSet = clientStmt.executeQuery(statement);

                serverStmt = serverConnection.createStatement();
                serverResultSet = serverStmt.executeQuery(statement);

                String tableContentToCompare = getContentToCompare(tableName, type);
                String clientTableContent = resultSetToString(clientResultSet);
                Assert.assertEquals(tableContentToCompare, clientTableContent);

                if (type2 != null) {
                    tableContentToCompare = getContentToCompare(tableName, type2);
                }
                String serverTableContent = resultSetToString(serverResultSet);

                Assert.assertEquals(tableContentToCompare, serverTableContent);

                ResultSetHelper.assertEquals(clientResultSet, serverResultSet);
            } catch (SQLException e) {
                throw e;
            } finally {
                if (clientStmt != null) {
                    clientStmt.close();
                }

                if (serverStmt != null) {
                    serverStmt.close();
                }

                if (contentRelatedToStrategyStmt != null) {
                    contentRelatedToStrategyStmt.close();
                }
            }
        }
    }

    private String getContentToCompare(String tableName, ConnectionType type) {
        if (type == ConnectionType.CLIENT) {
            return clientTableContentMap.get(tableName);
        } else {
            return serverTableContentMap.get(tableName);
        }
    }

    public static String resultSetToString(final ResultSet rs) throws SQLException {
        StringBuilder strBuilder = new StringBuilder();

        while (rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();

            List<String> sortedColumnNames = getSortedColumnNames(metaData);

            for (String columnName : sortedColumnNames) {
                strBuilder.append(columnName).append("(").append(rs.getObject(columnName)).append("),");
            }
        }
        return strBuilder.toString();
    }

    private static List<String> getSortedColumnNames(ResultSetMetaData metaData) throws SQLException {
        List<String> sortedList = new ArrayList<String>();

        for (int j = 1; j <= metaData.getColumnCount(); j++) {
            sortedList.add(metaData.getColumnName(j));
        }

        Collections.sort(sortedList);
        return sortedList;
    }

    public void storeTableContent(Map<String, String> tableStatementMap) throws SQLException {

        storeTableContentMap(tableStatementMap, clientTableContentMap, ConnectionType.CLIENT);
        storeTableContentMap(tableStatementMap, serverTableContentMap, ConnectionType.SERVER);
    }

    private void storeTableContentMap(Map<String, String> tableStatementMap, Map<String, String> tableContentMap,
        ConnectionType type) throws SQLException {
        Statement contentRelatedToStrategyStmt = null;
        try {
            final Connection connection = getConnectionFromType(type);

            for (String tableName : tableStatementMap.keySet()) {
                contentRelatedToStrategyStmt = connection.createStatement();
                final ResultSet contentRelatedToStrategyRS = contentRelatedToStrategyStmt.executeQuery(
                    tableStatementMap.get(tableName));
//                if (contentRelatedToStrategyRS.next()) {
                tableContentMap.put(tableName, resultSetToString(contentRelatedToStrategyRS));
//                } else {
//                    tableContentMap.put(tableName, "");
//                }
            }

        } finally {
            if (contentRelatedToStrategyStmt != null) {
                contentRelatedToStrategyStmt.close();
            }
        }
    }
}
