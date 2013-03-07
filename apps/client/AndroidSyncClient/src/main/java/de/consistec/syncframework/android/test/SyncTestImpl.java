package de.consistec.syncframework.android.test;

/*
 * #%L
 * doppelganger
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
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.GenericDatabaseAdapter;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import dalvik.system.PathClassLoader;
import de.mindpipe.android.logging.log4j.LogConfigurator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 27.06.12 09:16
 * <p/>
 *
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SyncTestImpl {

    // The buffer size to read database file
    public static final String PROPS_CLIENT_DB_PATH = "clientDatabasePath";
    private static final int READ_BUFFER_SIZE = 4096;
    private static final Logger LOG;
    private static final Config CONF = Config.getInstance();
    private AssetManager assetManager;
    private String clientDbPath;
    private Connection clientConnection;
    private Connection serverConnection;

    // configuring log4j logger
    static {
        final LogConfigurator logConfigurator = new LogConfigurator();

        logConfigurator.setFileName(
            Environment.getExternalStorageDirectory() + File.separator + "syncframework_test.log");
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("syncframework_test", Level.ALL);
        logConfigurator.configure();
        LOG = LoggerFactory.getLogger("syncframework_test");
    }

    public SyncTestImpl(AssetManager assetManager) {
        super();
        this.assetManager = assetManager;
        loadSettings();
    }

    private void loadSettings() {

        InputStream in = null;

        try {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1) {

                in = assetManager.open("gingerbread.properties");
                CONF.setClientDatabaseAdapter(
                    de.consistec.syncframework.android.adapter.GingerbreadSQLiteDatabaseAdapter.class);

            } else if (Build.VERSION.SDK_INT >= 14) {
                // ICS
                in = assetManager.open("ics.properties");
                CONF.setClientDatabaseAdapter(
                    de.consistec.syncframework.android.adapter.ICSSQLiteDatabaseAdapter.class);
            } else {
                throw new UnsupportedOperationException("Platform not supported");
            }
            CONF.init(in);

        } catch (IOException e) {
            LOG.error("Error while loading settings", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    private void handleException(Exception e) throws SyncException {
        throw new SyncException(e);
    }

    public Connection getClientConnection() {
        try {
            if (clientConnection != null && !clientConnection.isClosed()) {
                clientConnection.close();
                clientConnection = null;
            }
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1) {

                PathClassLoader pathLoad = AccessController.doPrivileged(new SyncTestClassLoader());

                Class.forName(CONF.getClientDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_DRIVER_NAME,
                    "SQLite.JDBCDriver"), true, pathLoad);

                clientConnection = DriverManager.getConnection(CONF.getClientDatabaseProperties().getProperty(
                    GenericDatabaseAdapter.PROPS_URL, "jdbc:sqlite:/sdcard/client.sl3"));

            } else if (Build.VERSION.SDK_INT >= 14) {

                // ICS
                Class.forName(CONF.getClientDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_DRIVER_NAME,
                    "org.sqldroid.SQLDroidDriver"));

                clientConnection = DriverManager.getConnection(CONF.getClientDatabaseProperties().getProperty(
                    GenericDatabaseAdapter.PROPS_URL, "jdbc:sqldroid:/sdcard/client.sl3"));

            } else {
                throw new UnsupportedOperationException("Platform not supported");
            }
            return clientConnection;

        } catch (SQLException e) {
            LOG.error("SQLEception occurred while creating sqlite connection", e);
        } catch (ClassNotFoundException e) {
            LOG.error("Could not load jdbc driver for SQLite", e);
        }
        return null;
    }

    public Connection getServerConnection() {
        try {
            if (serverConnection != null && !serverConnection.isClosed()) {
                serverConnection.close();
                serverConnection = null;
            }

            Class.forName(CONF.getServerDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_DRIVER_NAME,
                "org.postgresql.Driver"));
            serverConnection = DriverManager.getConnection(
                CONF.getServerDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_URL,
                "jdbc:postgresql://10.0.2.2/server"),
                CONF.getServerDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_SYNC_USERNAME, "syncuser"),
                CONF.getServerDatabaseProperties().getProperty(GenericDatabaseAdapter.PROPS_SYNC_PASSWORD, "syncuser"));

            return serverConnection;

        } catch (SQLException e) {
            LOG.error("SQLEception occurred while creating sqlite connection", e);
        } catch (ClassNotFoundException e) {
            LOG.error("Culd not load jdbc driver for SQLite", e);
        }
        return null;
    }

    public InputStream getResourceAsStream(String resourceName) {
        try {
            return assetManager.open(resourceName);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public void resetClientAndServerDatabase() throws SyncException, ContextException {
        OutputStream os = null;
        InputStream is = null;
        try {
            File clientDb = new File(clientDbPath);
            if (clientDb.exists() && !clientDb.delete()) {
                throw new IOException("could not delete client database file");
            }
            is = getResourceAsStream("client.sl3");
            os = new FileOutputStream(clientDbPath);
            byte[] buffer = new byte[READ_BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {

                os.write(buffer, 0, bytesRead);
            }

            LOG.debug("Dropping tables...");
            dropTable(getServerConnection(), "categories_md");
            dropTable(getServerConnection(), "categories");
            dropTable(getServerConnection(), "items");
            dropTable(getServerConnection(), "items_md");

            LOG.debug("Creating tables...");
            Statement stmt = getServerConnection().createStatement();
            stmt.execute(
                "create table categories (\"categoryid\" INTEGER NOT NULL PRIMARY KEY ,\"categoryname\" "
                + "VARCHAR (30000),\"description\" VARCHAR (30000))");
            stmt.close();
            stmt = getServerConnection().createStatement();
            stmt.execute(
                "create table items (\"itemid\" INTEGER NOT NULL PRIMARY KEY ,\"itemname\" "
                + "VARCHAR (30000),\"description\" VARCHAR (30000))");
            stmt.close();
            // TODO: replace sync() with the current synchronization method
            // sync();

        } catch (IOException e) {
            handleException(e);
        } catch (SecurityException e) {
            handleException(e);
        } catch (IllegalArgumentException e) {
            handleException(e);
        } catch (SQLException e) {
            handleException(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                handleException(e);
            }
        }
    }

    private void dropTable(Connection serverConn, String tableName) throws SyncException {
        try {
            Statement dropTable = serverConn.createStatement();
            dropTable.execute(String.format("drop table if exists %s", tableName));
            dropTable.close();
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private static class SyncTestClassLoader implements PrivilegedAction<PathClassLoader> {

        @Override
        public PathClassLoader run() {
            return new PathClassLoader("/system/framework/sqlite-jdbc.jar", ClassLoader.getSystemClassLoader());
        }
    }
}
