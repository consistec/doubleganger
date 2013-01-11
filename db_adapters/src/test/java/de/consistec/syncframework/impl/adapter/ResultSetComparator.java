package de.consistec.syncframework.impl.adapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.Assert;

/**
 * This class has only one purpose - to assertEquality values in two ResultSets.
 *
 * @author Marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.10.12 16:42
 * @since 0.0.1-SNAPSHOT
 */
public class ResultSetComparator {

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

    public static void assertEquals(String content1, String content2) throws SQLException {

        Assert.assertEquals(content1, content2);
    }
}
