package de.consistec.doubleganger.android.test;

/*
 * #%L
 * doubleganger
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

import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

/**
 * The Class de.consistec.doubleganger.android.test.AllDatabaseOperationsSQLite.
 * <p/>
 * @author Markus Backes
 */
public class AllDatabaseOperationsSQLite extends InstrumentationTestCase {

    /**
     * The client database path.
     */
    private String clientDbPath = "/sdcard/client.sl3";
    private SyncTestImpl syncTest = null;

    /**
     * Instantiates a new all database operations.
     */
    public AllDatabaseOperationsSQLite() {
    }

    private AssetManager getAssetManager() {
        Resources resources = AllDatabaseOperationsSQLite.this.getInstrumentation().getContext().getResources();
        return resources.getAssets();
    }

    @Override
    protected void setUp() throws Exception { //NOSONAR
        super.setUp();
        if (syncTest == null) {
            syncTest = new SyncTestImpl(getAssetManager());
        }
    }

    /**
     *
     * @see android.de.consistec.doubleganger.android.test.InstrumentationTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception { //NOSONAR
        super.tearDown();
        File clientDb = new File(clientDbPath);
        if (clientDb.exists() && !clientDb.delete()) {
            throw new IOException("could not delete client database file");
        }
    }

    public Connection getClientConnection() {
        return syncTest.getClientConnection();
    }

    public Connection getServerConnection() {
        return syncTest.getServerConnection();
    }

    public InputStream getResourceAsStream(String resourceName) {
        return syncTest.getResourceAsStream(resourceName);
    }

    public void resetClientAndServerDatabase() throws SyncException, ContextException {
        syncTest.resetClientAndServerDatabase();
    }

}
