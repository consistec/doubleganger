package de.consistec.doubleganger.common.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - DumbDbAdapter.java
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
import de.consistec.doubleganger.common.data.schema.Column;
import de.consistec.doubleganger.common.data.schema.ISQLConverter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.doubleganger.common.util.HashCalculator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Dump IDatabaseAdapter implementation for mocking.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
 * @date 31.10.2012 14:48:35
 * @since 0.0.1-SNAPSHOT
 */
public class DumbDbAdapter implements IDatabaseAdapter {

    public Connection connection;

    private DumbDbAdapter() {
    }

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
    public void getChanges(String tableName, DatabaseAdapterCallback<ResultSet> callback) throws
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
    public HashCalculator getHashCalculator() {
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
}
