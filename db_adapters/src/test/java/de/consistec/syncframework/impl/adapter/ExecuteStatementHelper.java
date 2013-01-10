package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.util.CollectionsUtil.newArrayList;
import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.exception.SyncException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public ExecuteStatementHelper(Connection clientConnection, Connection serverConnection) {
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
    public void executeStatementAndCompareResults(Map<String, String> statementsToExecute,
        ResultSetComparator comparator, Config conf, ConnectionType type,
        ConnectionType type2) throws
        SQLException, SyncException {

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
                comparator.compare(tableContentToCompare, clientTableContent);

                if (type2 != null) {
                    tableContentToCompare = getContentToCompare(tableName, type2);
                }
                String serverTableContent = resultSetToString(serverResultSet);

                comparator.compare(tableContentToCompare, serverTableContent);

                comparator.compare(clientResultSet, serverResultSet);
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

    private String resultSetToString(final ResultSet rs) throws SQLException {
        if (rs == null) {
            throw new IllegalArgumentException("Passed result set can't be null!!!");
        }
        StringBuilder strBuilder = new StringBuilder();

        while (rs.next()) {
            ResultSetMetaData metaData = rs.getMetaData();

            List<String> sortedList = newArrayList();
            sortColumnNames(metaData, sortedList);

            for (String listEntry : sortedList) {
                strBuilder.append(listEntry).append("(").append(rs.getObject(listEntry)).append("),");
            }
        }
        return strBuilder.toString();
    }

    private List<String> sortColumnNames(ResultSetMetaData metaData, List<String> sortedList) throws SQLException {

        int columnCount = metaData.getColumnCount();
        for (int j = 1; j <= columnCount; j++) {

            sortedList.add(metaData.getColumnName(j));
        }

        Collections.sort(sortedList);
        return sortedList;
    }

    public void readTableContent(Map<String, String> tableStatementMap) throws
        SQLException {

        fillTableContentMap(tableStatementMap, clientTableContentMap, ConnectionType.CLIENT);
        fillTableContentMap(tableStatementMap, serverTableContentMap, ConnectionType.SERVER);
    }

    private void fillTableContentMap(Map<String, String> tableStatementMap, Map<String, String> tableContentMap,
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
