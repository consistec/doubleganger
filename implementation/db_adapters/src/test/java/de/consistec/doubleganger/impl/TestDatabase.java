package de.consistec.doubleganger.impl;

/*
 * #%L
 * Project - doubleganger
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
import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory;
import de.consistec.doubleganger.common.adapter.DatabaseAdapterFactory.AdapterPurpose;
import de.consistec.doubleganger.common.adapter.IDatabaseAdapter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;
import de.consistec.doubleganger.common.util.LoggingUtil;
import de.consistec.doubleganger.impl.adapter.DummyDataSource;
import de.consistec.doubleganger.impl.adapter.DummyDataSource.SupportedDatabases;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.slf4j.cal10n.LocLogger;

public class TestDatabase {

    private static final LocLogger LOGGER = LoggingUtil.createLogger(TestDatabase.class.getCanonicalName());
    private final SupportedDatabases supportedDb;
    private DummyDataSource dataSource;
    private Connection connection;
    private DatabaseAdapterFactory.AdapterPurpose side;
    private boolean isTriggersActivated;

    public TestDatabase(DummyDataSource.SupportedDatabases supportedDb, DatabaseAdapterFactory.AdapterPurpose side,
        boolean isTriggersActivated) {
        this.supportedDb = supportedDb;
        this.side = side;
        this.isTriggersActivated = isTriggersActivated;
    }

    /**
     * Loads the properties, creates the datasources and connects as the syncuser by default.
     * Call {@link connectWithExternalUserOnServer()} if you activate the triggers on the server,
     * and/or {@link connectWithExternalUserOnClient()} if you activate the triggers on the client.
     */
    public void init() throws SQLException, IOException {
        manageConfigFile();
        manageTriggerActivation();

        dataSource = new DummyDataSource(supportedDb, side);

        connectWithSyncUser();
    }

    public void connectWithSyncUser() throws SQLException {
        connectWithUser(dataSource.getSyncUserName(), dataSource.getSyncUserPassword());
    }

    public void connectWithExternalUser() throws SQLException {
        connectWithUser(dataSource.getExternUserName(), dataSource.getExternUserPassword());
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

    private void manageConfigFile() throws SQLException, IOException {
        switch (supportedDb) {
            case MYSQL:
                Config.getInstance().init(getClass().getResourceAsStream("/config_mysql.properties"));
                break;
            case POSTGRESQL:
                Config.getInstance().init(getClass().getResourceAsStream("/config_postgre.properties"));
                break;
            case SQLITE:
                Config.getInstance().init(getClass().getResourceAsStream("/config_sqlite.properties"));
                break;
            default:
                throw new IllegalArgumentException(supportedDb.name());
        }
    }

    private void manageTriggerActivation() {
        switch (side) {
            case CLIENT:
                Config.getInstance().setSqlTriggerOnClientActivated(isTriggersActivated);
                break;
            case SERVER:
                Config.getInstance().setSqlTriggerOnServerActivated(isTriggersActivated);
                break;
            default:
                throw new IllegalArgumentException(side.name());
        }
    }

    @Override
    public String toString() {
        return "TestDatabase: " + supportedDb.name() + ", side: " + side + ", triggers active: " + isTriggersActivated;
    }
}
