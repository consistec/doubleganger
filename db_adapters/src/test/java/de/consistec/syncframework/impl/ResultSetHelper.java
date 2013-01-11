package de.consistec.syncframework.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
