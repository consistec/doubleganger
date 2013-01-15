package de.consistec.syncframework.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;

/**
 * This class helps on operation with ResultSets (comparison, equality, serialization)
 *
 * @author Marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.10.12 16:42
 * @since 0.0.1-SNAPSHOT
 */
public class ResultSetHelper {

    public static Map<Integer, Map<String, String>> resultSetToMap(final ResultSet rs) throws SQLException {
        Map<Integer, Map<String, String>> result = new HashMap<Integer, Map<String, String>>();
        int rowCount = 0;

        while (rs.next()) {
            Map<String, String> columnValue = new HashMap<String, String>();
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                String columnName = metaData.getColumnName(i);
                columnValue.put(columnName, (String) rs.getObject(columnName));
            }
            result.put(rowCount, columnValue);
        }
        return result;
    }

    public static String getExpectedResultSet(ResultSet serverRs, ResultSet clientRs, String mask) throws SQLException {
        StringBuilder strBuilder = new StringBuilder();
        ResultSetMetaData metaData;

        for (char character : mask.toCharArray()) {
            clientRs.next();
            serverRs.next();
            if (character == 'S') {
                // server row is expected
                metaData = serverRs.getMetaData();
                List<String> sortedColumnNames = getSortedColumnNames(metaData);

                for (String columnName : sortedColumnNames) {
                    strBuilder.append(columnName).append("(").append(serverRs.getObject(columnName)).append("),");
                }
            }

            if (character == 'C') {
                // client row is expected
                metaData = clientRs.getMetaData();
                List<String> sortedColumnNames = getSortedColumnNames(metaData);

                for (String columnName : sortedColumnNames) {
                    strBuilder.append(columnName).append("(").append(clientRs.getObject(columnName)).append("),");
                }
            }
        }
        return strBuilder.toString();
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

    public static boolean assertEquals(ResultSet source, ResultSet target) throws SQLException {

        while (source.next() && target.next()) {
            int columnCountSource = source.getMetaData().getColumnCount();
            int columnCountTarget = target.getMetaData().getColumnCount();

            Assert.assertEquals(columnCountSource, columnCountTarget);

            for (int i = 1; i <= columnCountSource; i++) {
                if (!source.getObject(i).equals(target.getObject(i))) {
                    return false;
                }
            }

            // source and target must reach the last row in the same iteration
            if (source.isLast() != target.isLast()) {
                return false;
            }
        }
        return true;
    }
}
