package de.consistec.syncframework.impl.adapter;

/*
 * #%L
 * Project - doppelganger
 * File - PostgresThrowsExceptionTest.java
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

import static org.junit.Assert.fail;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.consistec.syncframework.common.adapter.DatabaseAdapterCallback;
import de.consistec.syncframework.common.data.schema.Column;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.exception.database_adapter.TransactionAbortedException;
import de.consistec.syncframework.common.exception.database_adapter.UniqueConstraintException;
import de.consistec.syncframework.common.util.CollectionsUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 11.01.13 14:22
 */
public class PostgresThrowsExceptionTest {

    //<editor-fold defaultstate="expanded" desc=" Class fields " >
    private static final SQLException TRANSACTION_EXCEPTION = new SQLException("test transaction aborted exception",
        "400001");
    private static final SQLException UNIQUE_EXCEPTION = new SQLException("test unique exception", "23505");
//</editor-fold>

    @Mock
    private Connection connectionMock;
    @Mock
    private PreparedStatement statementMock;
    @Mock
    private PostgresDatabaseAdapter adapterMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adapterMock.connection = connectionMock;
    }

    @Test(expected = TransactionAbortedException.class)
    public void commit() throws DatabaseAdapterException {

        try {
            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).commit();
            doThrow(TRANSACTION_EXCEPTION).when(
                connectionMock).commit();

            adapterMock.init(connectionMock);
            adapterMock.commit();

            verify(adapterMock).commit();

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {

                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void getRowForPrimaryKey() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";
            DatabaseAdapterCallback callback = new DatabaseAdapterCallback<ResultSet>() {
                @Override
                public void onSuccess(final ResultSet result) throws DatabaseAdapterException, SQLException {
                    // do nothing
                }
            };

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).getRowForPrimaryKey(pk, tableName, callback);

            when(adapterMock.getPrimaryKeyColumn(isA(String.class))).thenReturn(new Column("id", 0));
            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeQuery()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.getRowForPrimaryKey(pk, tableName, callback);

            verify(adapterMock).getRowForPrimaryKey(pk, tableName, callback);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void updateMdRow() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";
            int rev = 1;
            int flag = 0;
            String mdv = "8798786876876fdsaf78678";

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).updateMdRow(rev, flag, pk, mdv, tableName);

            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.updateMdRow(rev, flag, pk, mdv, tableName);

            verify(adapterMock).updateMdRow(rev, flag, pk, mdv, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void deleteMdRow() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).deleteRow(pk, tableName);

            when(adapterMock.getPrimaryKeyColumn(isA(String.class))).thenReturn(new Column("id", 0));
            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.deleteRow(pk, tableName);

            verify(adapterMock).deleteRow(pk, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void insertMdRow() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";
            int rev = 1;
            int flag = 0;
            String mdv = "8798786876876fdsaf78678";

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).insertMdRow(rev, flag, pk, mdv, tableName);

            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.insertMdRow(rev, flag, pk, mdv, tableName);

            verify(adapterMock).insertMdRow(rev, flag, pk, mdv, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = UniqueConstraintException.class)
    public void insertMdRowUniqueConstraintException() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";
            int rev = 1;
            int flag = 0;
            String mdv = "8798786876876fdsaf78678";

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).insertMdRow(rev, flag, pk, mdv, tableName);

            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(UNIQUE_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.insertMdRow(rev, flag, pk, mdv, tableName);

            verify(adapterMock).insertMdRow(rev, flag, pk, mdv, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof UniqueConstraintException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void insertDataRow() throws DatabaseAdapterException {
        try {
            Map<String, Object> data = CollectionsUtil.newHashMap();
            data.put("categoryid", Integer.valueOf(1));
            data.put("description", "test description");
            data.put("categoryname", "test name");

            String tableName = "categories";

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).insertDataRow(data, tableName);

            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.insertDataRow(data, tableName);

            verify(adapterMock).insertDataRow(data, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }

    @Test(expected = TransactionAbortedException.class)
    public void updateDataRow() throws DatabaseAdapterException {
        try {
            Object pk = new Object();
            String tableName = "categories";
            Map<String, Object> data = CollectionsUtil.newHashMap();
            data.put("categoryid", Integer.valueOf(1));
            data.put("description", "test description");
            data.put("categoryname", "test name");

            doCallRealMethod().when(adapterMock).init(connectionMock);
            doCallRealMethod().when(adapterMock).updateDataRow(data, pk, tableName);

            when(adapterMock.getPrimaryKeyColumn(isA(String.class))).thenReturn(new Column("id", 0));
            when(connectionMock.prepareStatement(isA(String.class))).thenReturn(statementMock);
            when(statementMock.executeUpdate()).thenThrow(TRANSACTION_EXCEPTION);

            adapterMock.init(connectionMock);

            adapterMock.updateDataRow(data, pk, tableName);

            verify(adapterMock).updateDataRow(data, pk, tableName);

        } catch (DatabaseAdapterException e) {
            if (e instanceof TransactionAbortedException) {
                throw e;
            } else {
                fail("There should be a TransactionAbortedException thrown");
            }
        } catch (SQLException e) {
            fail("There should be a TransactionAbortedException thrown");
        }
    }
}
