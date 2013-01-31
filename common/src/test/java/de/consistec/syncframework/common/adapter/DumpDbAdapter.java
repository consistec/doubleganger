package de.consistec.syncframework.common.adapter;

import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.data.schema.ISQLConverter;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.TransactionAbortedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Dump IDatabaseAdapter implementation for mocking.
 *
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 31.10.2012 14:48:35
 * @since 0.0.1-SNAPSHOT
 */
public class DumpDbAdapter implements IDatabaseAdapter {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    public Connection connection;
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class constructors " >
    private DumpDbAdapter() {
    }

    //</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class methods " >
    @Override
    public void init(Properties adapterConfig) {
    }

    @Override
    public void init(Connection connection) {
        this.connection = connection;
    }

    @Override
    public ISQLConverter getSchemaConverter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getChangesForRevision(int revision, String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getChangesByFlag(String tableName, DatabaseAdapterCallback<ResultSet> callback) throws
        DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int updateRevision(int revision, String tableName, Object primaryKey) throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getRowForPrimaryKey(Object primaryKey, String tableName, DatabaseAdapterCallback<ResultSet> callback)
        throws DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getDeletedRowsForTable(String tableName, DatabaseAdapterCallback<ResultSet> callback) throws
        DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getAllRowsFromTable(String table, DatabaseAdapterCallback<ResultSet> callback) throws
        DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Schema getSchema() throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createMDSchemaOnServer() throws DatabaseAdapterException {
    }

    @Override
    public void createMDSchemaOnClient() throws DatabaseAdapterException {
    }

    @Override
    public void updateMdRow(int rev, int f, Object pk, String mdv, String tableName) throws DatabaseAdapterException,
        TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertMdRow(int rev, int f, Object pk, String mdv, String tableName) throws DatabaseAdapterException,
        TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateDataRow(Map<String, Object> data, Object primaryKey, String tableName) throws
        DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertDataRow(Map<String, Object> data, String tableName) throws DatabaseAdapterException,
        TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteRow(Object primaryKey, String tableName) throws DatabaseAdapterException,
        TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getLastRevision() throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNextRevision() throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Column getPrimaryKeyColumn(String table) throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getColumnNamesFromTable(String tableName) throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void applySchema(Schema s) throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasSchema() throws DatabaseAdapterException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Connection getConnection() {
//        throw new UnsupportedOperationException("Not supported yet.");
        return this.connection;
    }

    @Override
    public void commit() throws DatabaseAdapterException, TransactionAbortedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean existsMDTable(final String tableName) throws DatabaseAdapterException {
        return false;
    }

    @Override
    public void createMDTableOnServer(final String tableName) throws DatabaseAdapterException {
    }

    @Override
    public void createMDTableOnClient(final String tableName) throws DatabaseAdapterException {
    }
//</editor-fold>
}
