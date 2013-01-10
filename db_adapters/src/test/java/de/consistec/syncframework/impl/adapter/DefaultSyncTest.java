package de.consistec.syncframework.impl.adapter;

import static de.consistec.syncframework.common.SyncDirection.BIDIRECTIONAL;
import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static de.consistec.syncframework.impl.adapter.ConnectionType.CLIENT;
import static de.consistec.syncframework.impl.adapter.ConnectionType.SERVER;

import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import java.sql.SQLException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 14:33
 */
public abstract class DefaultSyncTest extends AbstractSyncTest {

//<editor-fold defaultstate="expanded" desc=" Class fields " >
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class constructors " >
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc=" Class accessors and mutators " >
//</editor-fold>
//<editor-fold defaultstate="expanded" desc=" Class methods " >
//</editor-fold>
    private void UcUc() throws SyncException, SQLException,
        ContextException {
        populateWithTestData();
        sync(BIDIRECTIONAL, SERVER_WINS, CLIENT);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcUc() throws SyncException, SQLException, ContextException {
        UcUc();
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testAddUc() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initAndSyncClient(query, BIDIRECTIONAL, SERVER_WINS, CLIENT);
        initAndSyncClient(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER);

        initAndSyncClient(query, BIDIRECTIONAL, FIRE_EVENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        testInvalidState(query);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void testInvalidState(String query) throws SyncException, SQLException, ContextException {
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void testInvalidState(String query1, String query2) throws SyncException, SQLException,
        ContextException {
        initClientAndServerWithSync(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModUc() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";

        initAndSyncClient(query, BIDIRECTIONAL, SERVER_WINS, CLIENT);
        initAndSyncClient(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER);

        initAndSyncClient(query, BIDIRECTIONAL, FIRE_EVENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testModUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        testInvalidState(query);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelUc() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";

        initAndSyncClient(query, BIDIRECTIONAL, SERVER_WINS, CLIENT);
        initAndSyncClient(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER);

        initAndSyncClient(query, BIDIRECTIONAL, FIRE_EVENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";

        testInvalidState(query);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcAdd() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";

        initAndSyncServer(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initAndSyncServer(query, BIDIRECTIONAL, CLIENT_WINS, SERVER);
        initAndSyncServer(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);
        initAndSyncServer(query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);

        initAndSyncServer(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";

        testInvalidState(query);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testAddAdd() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initClientAndServerAndSync(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerAndSync(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerAndSync(query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerAndSync(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerAndSync(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        testInvalidState(query, query);
    }

    // was already ignored
    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Ignore
    public void testModAdd() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        String resource2 = "category7_a_insert.xml";
        String query2 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        String resource2 = "category7_a_insert.xml";
        String query2 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        testInvalidState(query1, query2);
    }

    // was already ignored
    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Ignore
    public void testDelAdd() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String query1 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String query1 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcMod() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initAndSyncServer(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initAndSyncServer(query, BIDIRECTIONAL, CLIENT_WINS, SERVER);
        initAndSyncServer(query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initAndSyncServer(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);

        initAndSyncServer(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcModInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        testInvalidState(query);
    }

    // was already ignored
    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Ignore
    public void testAddMod() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_b_update.xml";
        String query2 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_b_update.xml";
        String query2 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModMod() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String resource2 = "category6_c_update.xml";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6c', description = 'uhhhhhhh 6c' "
            + "WHERE categoryid = 6";

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String resource2 = "category6_c_update.xml";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6c', description = 'uhhhhhhh 6c' "
            + "WHERE categoryid = 6";
        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelMod() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String resource2 = "category6_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 6";

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String resource2 = "category6_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 6";

        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcDel() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";

        initAndSyncServer(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initAndSyncServer(query, BIDIRECTIONAL, CLIENT_WINS, SERVER);
        initAndSyncServer(query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initAndSyncServer(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);

        initAndSyncServer(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";

        testInvalidState(query);
    }

    // was already ignored
    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Ignore
    public void testAddDel() throws SyncException, SQLException, ContextException {
        // pass if Add and Del passes
        String resource1 = "category7_a_insert.xml";
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String resource2 = "category7_a_delete.xml";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";
        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModDel() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_a_delete.xml";
        String query1 = "DELETE FROM categories WHERE categoryid = 6";
        String resource2 = "category6_b_update.xml";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSync(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSync(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSync(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_a_delete.xml";
        String query1 = "DELETE FROM categories WHERE categoryid = 6";
        String resource2 = "category6_b_update.xml";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        testInvalidState(query1, query2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelDel() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";
        initClientAndServerAndSync(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerAndSync(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerAndSync(query, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerAndSync(query, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);


        initClientAndServerAndSync(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        String query = "DELETE FROM categories WHERE categoryid = 6";
        testInvalidState(query);
    }
}
