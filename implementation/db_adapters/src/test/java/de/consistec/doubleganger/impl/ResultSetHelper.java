package de.consistec.doubleganger.impl;

/*
 * #%L
 * Project - doppelganger
 * File - ResultSetHelper.java
 * %%
 * Copyright (C) 2011 - 2013 consistec GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
 * @company consistec Engineering and Consulting GmbH
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
