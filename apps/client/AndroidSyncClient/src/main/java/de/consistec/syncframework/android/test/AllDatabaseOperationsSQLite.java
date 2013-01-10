package de.consistec.syncframework.android.test;

import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.impl.adapter.ISyncTests;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The Class de.consistec.syncframework.android.test.AllDatabaseOperationsSQLite.
 * <p/>
 * @author Markus Backes
 */
public class AllDatabaseOperationsSQLite extends InstrumentationTestCase implements ISyncTests {

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
     * @see android.de.consistec.syncframework.android.test.InstrumentationTestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception { //NOSONAR
        super.tearDown();
        File clientDb = new File(clientDbPath);
        if (clientDb.exists() && !clientDb.delete()) {
            throw new IOException("could not delete client database file");
        }
    }

    @Override
    public Connection getClientConnection() {
        return syncTest.getClientConnection();
    }

    @Override
    public Connection getServerConnection() {
        return syncTest.getServerConnection();
    }

    @Override
    public InputStream getResourceAsStream(String resourceName) {
        return syncTest.getResourceAsStream(resourceName);
    }

    @Override
    public void resetClientAndServerDatabase() throws SyncException, ContextException {
        syncTest.resetClientAndServerDatabase();
    }

    @Override
    public void testUcUc() throws SyncException, SQLException, ContextException {
        syncTest.testUcUc();
    }

    @Override
    public void testAddUc() throws SyncException, SQLException, ContextException {
        syncTest.testAddUc();
    }

    @Override
    public void testModUc() throws SyncException, SQLException, ContextException {
        syncTest.testModUc();
    }

    @Override
    public void testDelUc() throws SyncException, SQLException, ContextException {
        syncTest.testDelUc();
    }

    @Override
    public void testUcAdd() throws SyncException, SQLException, ContextException {
        syncTest.testUcAdd();
    }

    @Override
    public void testAddAdd() throws SyncException, SQLException, ContextException {
        syncTest.testAddAdd();
    }

    @Override
    public void testModAdd() throws SyncException, SQLException, ContextException {
        syncTest.testModAdd();
    }

    @Override
    public void testDelAdd() throws SyncException, SQLException, ContextException {
        syncTest.testDelAdd();
    }

    @Override
    public void testUcMod() throws SyncException, SQLException, ContextException {
        syncTest.testUcMod();
    }

    @Override
    public void testAddMod() throws SyncException, SQLException, ContextException {
        syncTest.testAddMod();
    }

    @Override
    public void testModMod() throws SyncException, SQLException, ContextException {
        syncTest.testModMod();
    }

    @Override
    public void testDelMod() throws SyncException, SQLException, ContextException {
        syncTest.testDelMod();
    }

    @Override
    public void testUcDel() throws SyncException, SQLException, ContextException {
        syncTest.testUcDel();
    }

    @Override
    public void testAddDel() throws SyncException, SQLException, ContextException {
        syncTest.testAddDel();
    }

    @Override
    public void testModDel() throws SyncException, SQLException, ContextException {
        syncTest.testModDel();
    }

    @Override
    public void testDelDel() throws SyncException, SQLException, ContextException {
        syncTest.testDelDel();
    }

    @Override
    public Connection getExternalClientConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Connection getExternalServerConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
