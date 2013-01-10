package de.consistec.syncframework.impl.adapter;

import static junit.framework.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class has only one purpose - to compare values in two ResultSets.
 *
 * @author Marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 26.10.12 16:42
 * @since 0.0.1-SNAPSHOT
 */
public class ResultSetComparator {

    public void compare(ResultSet rs1, ResultSet rs2) throws SQLException {

        int columnCountRs1 = rs1.getMetaData().getColumnCount();
        int columnCountRs2 = rs2.getMetaData().getColumnCount();

        assertEquals(columnCountRs1, columnCountRs2);

        while (rs1.next() && rs2.next()) {
            for (int i = 1; i <= columnCountRs1; i++) {
                String colName = rs1.getMetaData().getColumnName(i);
                assertEquals(rs1.getObject(colName), rs2.getObject(colName));
            }
        }
    }

    public void compare(String content1, String content2) throws SQLException {

        assertEquals(content1, content2);
    }
}
