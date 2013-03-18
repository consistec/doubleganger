package de.consistec.syncframework.impl;

/*
 * #%L
 * Project - doppelganger
 * File - TestDatabase.java
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
import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.syncframework.common.util.LoggingUtil;
import de.consistec.syncframework.impl.adapter.DummyDataSource;
import de.consistec.syncframework.impl.adapter.DummyDataSource.SupportedDatabases;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.cal10n.LocLogger;

public abstract class TestDatabase {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(TestDatabase.class.getCanonicalName());
    private final SupportedDatabases supportedDb;
    private final String configFile;
    private DummyDataSource dataSource;
    private Connection connection;
    private DatabaseAdapterFactory.AdapterPurpose side;

    public TestDatabase(String configFile, DummyDataSource.SupportedDatabases supportedDb, DatabaseAdapterFactory.AdapterPurpose side) {
        this.configFile = configFile;
        this.supportedDb = supportedDb;
        this.side = side;
    }

    /**
     * Loads the properties, creates the datasources and connects as the syncuser by default.
     * Call {@link connectWithExternalUserOnServer()} if you activate the triggers on the server,
     * and/or {@link connectWithExternalUserOnClient()} if you activate the triggers on the client.
     */
    public void init() throws SQLException, IOException {
        Config.getInstance().init(getClass().getResourceAsStream(configFile));

        dataSource = new DummyDataSource(supportedDb, side);

        connectWithSyncUser();
    }

    public void connectWithSyncUser() throws SQLException {
        connectWithUser(dataSource.getSyncUserName(),dataSource.getSyncUserPassword());
    }

    public void connectWithExternalUser() throws SQLException {
        connectWithUser(dataSource.getExternUserName(),dataSource.getExternUserPassword());
    }

    private void connectWithUser(String dbUsername, String dbPassword) throws SQLException {
        closeConnections();
        connection = dataSource.getConnection(dbUsername, dbPassword);
        LOGGER.debug("Connecting on " + side + " as user " + dbUsername);
    }

    public void closeConnections() throws SQLException {
        for (Connection cx : dataSource.getCreatedConnections()) {
            cx.close();
        }
    }

    public Connection getConnection() throws SQLException {
        return connection;
    }

    public String getConfigFile() {
        return configFile;
    }

    public SupportedDatabases getSupportedDb() {
        return supportedDb;
    }

    public DummyDataSource getDataSource() {
        return dataSource;
    }

    public DatabaseAdapterFactory.AdapterPurpose getAdapterPurpose() {
        return side;
    }

    public int[] dropTables(String[] tables) throws SQLException {
        String[] queries = new String[tables.length];
        for (int i = 0; i < tables.length; i++) {
            queries[i] = String.format("drop table if exists %s", tables[i]);
        }
        return executeQueries(queries);
    }

    public int executeUpdate(String query) throws SQLException {
        return executeQueries(new String[]{query})[0];
    }

    public int[] executeQueries(String[] queries) throws SQLException {
        final Statement statement = connection.createStatement();
        for (String query : queries) {
            statement.addBatch(query);
        }
        return statement.executeBatch();
    }

    @Override
    public String toString() {
        return "TestDatabase: " + supportedDb + ", " + configFile;
    }

    public void createSchema(Schema schema) throws DatabaseAdapterException, SQLException {
        IDatabaseAdapter adapter = DatabaseAdapterFactory.newInstance(side);
        adapter.init(getConnection());
        adapter.applySchema(schema);
        switch (side) {
            case CLIENT:
                adapter.createMDSchemaOnClient();
                break;
            case SERVER:
                adapter.createMDSchemaOnServer();
                break;
            default:
                throw new IllegalArgumentException("Unknown adapter purpose: " + side);
        }
    }
}
