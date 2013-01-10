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

    @Test
    public void testUcUc() throws SyncException, SQLException, ContextException {
        populateWithTestData();
        sync(BIDIRECTIONAL, SERVER_WINS);
        compareDatabases(CLIENT);
    }

    @Test
    public void testAddUc() throws SyncException, SQLException, ContextException {
        String query = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initAndSyncClient(query, BIDIRECTIONAL, SERVER_WINS, CLIENT);
        initAndSyncClient(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER);

        initAndSyncClient(query, BIDIRECTIONAL, FIRE_EVENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddUcInvalidState() throws SyncException, SQLException, ContextException {
        String query = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testModUc() throws SyncException, SQLException, ContextException {
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
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testDelUc() throws SyncException, SQLException, ContextException {
        String query = "DELETE FROM categories WHERE categoryid = 6";

        initAndSyncClient(query, BIDIRECTIONAL, SERVER_WINS, CLIENT);
        initAndSyncClient(query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER);

        initAndSyncClient(query, BIDIRECTIONAL, FIRE_EVENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelUcInvalidState() throws SyncException, SQLException, ContextException {
        String query = "DELETE FROM categories WHERE categoryid = 6";

        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testUcAdd() throws SyncException, SQLException, ContextException {
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";

        initAndSyncServer(query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initAndSyncServer(query, BIDIRECTIONAL, CLIENT_WINS, SERVER);
        initAndSyncServer(query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);
        initAndSyncServer(query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);

        initAndSyncServer(query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcAddInvalidState() throws SyncException, SQLException, ContextException {
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testAddAdd() throws SyncException, SQLException, ContextException {
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";

        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query, query, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query, query, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAddInvalidState() throws SyncException, SQLException, ContextException {
        String query = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initClientAndServerWithSyncAndCompare(query, query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query, query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Ignore
    public void testModAdd() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        String query2 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModAddInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        String query2 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Ignore
    public void testDelAdd() throws SyncException, SQLException, ContextException {
        String query1 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelAddInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testUcMod() throws SyncException, SQLException, ContextException {
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
        String query = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Ignore
    public void testAddMod() throws SyncException, SQLException, ContextException {
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddModInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
            + "WHERE categoryid = 7";
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testModMod() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6c', description = 'uhhhhhhh 6c' "
            + "WHERE categoryid = 6";

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModModInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6c', description = 'uhhhhhhh 6c' "
            + "WHERE categoryid = 6";
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testDelMod() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String query2 = "DELETE FROM categories WHERE categoryid = 6";

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelModInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        String query2 = "DELETE FROM categories WHERE categoryid = 6";

        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testUcDel() throws SyncException, SQLException, ContextException {
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

        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Ignore
    public void testAddDel() throws SyncException, SQLException, ContextException {
        // pass if Add and Del passes
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDelInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testModDel() throws SyncException, SQLException, ContextException {
        String query1 = "DELETE FROM categories WHERE categoryid = 6";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query1, query2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(query1, query2, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModDelInvalidState() throws SyncException, SQLException, ContextException {
        String query1 = "DELETE FROM categories WHERE categoryid = 6";
        String query2 = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
            + "WHERE categoryid = 6";
        initClientAndServerWithSyncAndCompare(query1, query2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query1, query2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test
    public void testDelDel() throws SyncException, SQLException, ContextException {
        String query = "DELETE FROM categories WHERE categoryid = 6";
        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(query, query, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(query, query, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);


        initClientAndServerWithSyncAndCompare(query, query, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelDelInvalidState() throws SyncException, SQLException, ContextException {
        String query = "DELETE FROM categories WHERE categoryid = 6";
        initAndSyncClient(query, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initAndSyncClient(query, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }
}
