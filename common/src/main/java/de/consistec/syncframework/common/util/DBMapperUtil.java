package de.consistec.syncframework.common.util;

import static de.consistec.syncframework.common.util.CollectionsUtil.newHashMap;

import de.consistec.syncframework.common.data.MDEntry;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 09.01.13 11:05
 */
public final class DBMapperUtil {

//<editor-fold defaultstate="expanded" desc=" Class fields " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class constructors " >

//</editor-fold>

//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >

//</editor-fold>

//<editor-fold defaultstate="expanded" desc=" Class methods " >

//</editor-fold>

    private static final int METADATA_COLUMN_COUNT = 4;

    private DBMapperUtil() {

    }

    /**
     * Maps the resultSet to an MDEntry object.
     *
     * @param resultSet the result set to map
     * @param tableName the name of the table which is represented through the MDEntry.
     * @return MDEntry object with the values of the result set.
     * @throws SQLException
     */
    public static MDEntry getMetadata(ResultSet resultSet, String tableName) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();

        MDEntry tmpEntry = new MDEntry();
        tmpEntry.setTableName(tableName);

        String columnName;

        for (int i = 1; i <= METADATA_COLUMN_COUNT; i++) {
            columnName = meta.getColumnName(i);

            if ("rev".equalsIgnoreCase(columnName)) {
                tmpEntry.setRevision(resultSet.getInt(i));
            } else if ("pk".equalsIgnoreCase(columnName)) {
                tmpEntry.setPrimaryKey(resultSet.getObject(i));
            } else if ("mdv".equalsIgnoreCase(columnName)) {
                String mdv = resultSet.getString(i);
                if (StringUtil.isNullOrEmpty(mdv)) {
                    tmpEntry.setDeleted();
                } else {
                    tmpEntry.setModified();
                }
                tmpEntry.setMdv(mdv);
            } else if ("f".equalsIgnoreCase(columnName)) {
                tmpEntry.setAdded();
//                // do nothing, we don't want to sync the f flag
//                continue;
            }
        }
        return tmpEntry;
    }

    /**
     * Maps the result set to a map.
     *
     * @param resultSet the result set to map.
     * @return a map which key is the column name and value the value in the result set in the given column.
     * @throws SQLException
     */
    public static Map<String, Object> getRowData(ResultSet resultSet) throws SQLException {
        Map<String, Object> rowData = newHashMap();
        ResultSetMetaData meta = resultSet.getMetaData();

        int columnCount = meta.getColumnCount();

        for (int i = METADATA_COLUMN_COUNT + 1; i <= columnCount; i++) {
            rowData.put(meta.getColumnName(i), resultSet.getObject(i));
        }
        return rowData;
    }
}
