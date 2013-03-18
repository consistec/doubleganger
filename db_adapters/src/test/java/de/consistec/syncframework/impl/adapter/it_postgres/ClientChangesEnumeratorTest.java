package de.consistec.syncframework.impl.adapter.it_postgres;

/*
 * #%L
 * Project - doppelganger
 * File - ClientChangesEnumeratorTest.java
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

import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.SyncData;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.impl.TestDatabase;

import java.io.IOException;
import java.sql.SQLException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * This class tests the correct handling of getChanges for client side.
 *
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 13.12.12 12:16
 */
@RunWith(value = Parameterized.class)
public class ClientChangesEnumeratorTest extends ChangesEnumeratorTest {

    private static String[] serverInsertQueries = new String[]{
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (1, 'Beverages', 'Soft drinks')",
        "INSERT INTO categories (categoryid, categoryname, description) VALUES (2, 'Condiments', 'Sweet and ')",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (1, '8F3CCBD3FE5C9106253D472F6E36F0E1', 1, 1)",
        "INSERT INTO categories_md (rev, mdv, pk, f) VALUES (2, '75901F57520C09EB990837C7AA93F717', 2, 1)",};

    public ClientChangesEnumeratorTest(TestDatabase serverDb, TestDatabase clientDb) {
        super(serverDb, clientDb);
    }

    @Before
    public void init() throws IOException, SQLException {
        super.setUp();
        clientDb.executeQueries(serverInsertQueries);
    }

    @Test
    public void getChangesServerToClient() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        SyncData clientChanges = getChangesGlobalOnClient(ConflictStrategy.SERVER_WINS, SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.getChanges().isEmpty());
    }

    @Test
    public void getChangesClientToServer() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = getChangesGlobalOnClient(ConflictStrategy.CLIENT_WINS, SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesBidirectional() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = getChangesGlobalOnClient(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesServerToClientPerTable() throws ContextException, SyncException, DatabaseAdapterException,
        SQLException {
        SyncData clientChanges = getChangesPerTableOnClient(ConflictStrategy.SERVER_WINS,
            SyncDirection.SERVER_TO_CLIENT);
        assertTrue(clientChanges.getChanges().isEmpty());
    }

    @Test
    public void getChangesClientToServerPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = getChangesPerTableOnClient(ConflictStrategy.CLIENT_WINS,
            SyncDirection.CLIENT_TO_SERVER);
        assertTrue(clientChanges.getChanges().size() == 2);
    }

    @Test
    public void getChangesBidirectionalPerTable() throws SyncException, ContextException, SQLException,
        DatabaseAdapterException {

        SyncData clientChanges = getChangesPerTableOnClient(ConflictStrategy.CLIENT_WINS, SyncDirection.BIDIRECTIONAL);
        assertTrue(clientChanges.getChanges().size() == 2);
    }
}
