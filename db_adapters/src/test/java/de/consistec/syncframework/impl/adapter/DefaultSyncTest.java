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
import de.consistec.syncframework.impl.TestScenario;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 13.12.12 14:33
 */
@RunWith(value = Parameterized.class)
public abstract class DefaultSyncTest extends AbstractSyncTest {

    TestScenario scenario;
    // Scenarii:
    // Invalid State
    //   CLIENT_TO_SERVER, SERVER_WINS
    //   SERVER_TO_CLIENT, CLIENT_WINS
    // Test
    //   BIDIRECTIONAL, SERVER_WINS, CLIENT
    //   BIDIRECTIONAL, CLIENT_WINS, CLIENT
    //   CLIENT_TO_SERVER, CLIENT_WINS, CLIENT
    //   SERVER_TO_CLIENT, SERVER_WINS, CLIENT
    //   BIDIRECTIONAL, FIRE_EVENT, CLIENT
    static String queryDelete6 = "DELETE FROM categories WHERE categoryid = 6";
    static String queryUpdate6b = "UPDATE categories SET categoryid = 6, categoryname = 'Cat6b', description = 'uhhhhhhh 6b' "
        + "WHERE categoryid = 6";
    static String queryUpdate6c = "";
    static String queryDelete7 = "DELETE FROM categories WHERE categoryid = 7";
    static String queryInsert7a = "INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
    static String queryUpdate7b = "UPDATE categories SET categoryid = 7, categoryname = 'Cat7b', description = 'uhhhhhhh 7b' "
        + "WHERE categoryid = 7";
    static String queryUpdate7c = "";

//    @Parameters
    public static Collection<TestScenario> syncDirections() {
        TestScenario[] scenarii = new TestScenario[]{
//            new TestScenario("Unchanged Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT),
//            new TestScenario("Add Unchanged", BIDIRECTIONAL, SERVER_WINS, CLIENT).addStep(CLIENT, queryInsert7a),
//            new TestScenario("Add Unchanged", BIDIRECTIONAL, CLIENT_WINS, CLIENT).addStep(CLIENT, queryInsert7a),
//            new TestScenario("Add Unchanged", CLIENT_TO_SERVER, CLIENT_WINS, CLIENT).addStep(CLIENT, queryInsert7a),
//            new TestScenario("Add Unchanged", SERVER_TO_CLIENT, SERVER_WINS, SERVER).addStep(CLIENT, queryInsert7a),
//            new TestScenario("Add Unchanged", BIDIRECTIONAL, FIRE_EVENT, CLIENT).addStep(CLIENT, queryInsert7a)
        };
        return Arrays.asList(scenarii);
    }
//            {"", "", BIDIRECTIONAL, SERVER_WINS, CLIENT, null},
//            {"", "", BIDIRECTIONAL, CLIENT_WINS, CLIENT, null},
//            {"", "", CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, null},
//            {"", "", SERVER_TO_CLIENT, SERVER_WINS, CLIENT, SERVER},
//            {"", "", BIDIRECTIONAL, FIRE_EVENT, CLIENT, null},
//            // Exceptions
//            {"", "", CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null},
//            {"", "", SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null}};

//    public DefaultSyncTest(TestScenario scenario) {
//        this.scenario = scenario;
//    }

//    @Before
    public void init() throws SyncException, ContextException, SQLException {
        populateWithTestData();
    }

    @Test
    public void testUcUc() throws SQLException, ContextException, SyncException {
        populateWithTestData();
        sync(scenario.getDirection(), scenario.getStrategy());
        compareDatabases(CLIENT, null);
    }

    @Test
    public void testAddUc() throws SyncException, SQLException, ContextException {
//        populateWithTestData();
//        helper.executeUpdateOnClient(query);
//        sync(syncDirection, strategy);
//        compareDatabases(type, type2);
//
//        initAndSyncClientAndCompare(queryInsert7a, direction, strategy, type1, type2);
    }

    @Test
    public void testModUc() throws SyncException, SQLException, ContextException {
//        initAndSyncClientAndCompare(queryUpdate6b, direction, strategy, type1, type2);
    }

    @Test
    public void testDelUc() throws SyncException, SQLException, ContextException {
        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, SERVER_WINS, CLIENT, null);
        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, CLIENT_WINS, CLIENT, null);
        initAndSyncServerAndCompare(queryDelete6, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);
        initAndSyncServerAndCompare(queryDelete6, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, null);

        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, FIRE_EVENT, CLIENT, null);
    }

    @Test
    public void testUcAdd() throws SyncException, SQLException, ContextException {

        initAndSyncServerAndCompare(queryInsert7a, BIDIRECTIONAL, SERVER_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryInsert7a, BIDIRECTIONAL, CLIENT_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryInsert7a, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);
        initAndSyncServerAndCompare(queryInsert7a, SERVER_TO_CLIENT, SERVER_WINS, SERVER, null);

        initAndSyncServerAndCompare(queryInsert7a, BIDIRECTIONAL, FIRE_EVENT, SERVER, null);
    }

    @Test
    public void testAddAdd() throws SyncException, SQLException, ContextException {

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAddInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryInsert7a, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Ignore
    public void testModAdd() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);

        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Ignore
    public void testDelAdd() throws SyncException, SQLException, ContextException {
        String query1 = " INSERT INTO categories (categoryid, categoryname, description) VALUES (7, 'Cat7a', 'uhhhhhhh 7a')";
        String query2 = "DELETE FROM categories WHERE categoryid = 7";

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testUcMod() throws SyncException, SQLException, ContextException {
        initAndSyncServerAndCompare(queryUpdate6b, BIDIRECTIONAL, SERVER_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryUpdate6b, BIDIRECTIONAL, CLIENT_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryUpdate6b, SERVER_TO_CLIENT, SERVER_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryUpdate6b, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);

        initAndSyncServerAndCompare(queryUpdate6b, BIDIRECTIONAL, FIRE_EVENT, SERVER, null);
    }

    @Ignore
    public void testAddMod() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testModMod() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testDelMod() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testUcDel() throws SyncException, SQLException, ContextException {
        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, SERVER_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, CLIENT_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryDelete6, SERVER_TO_CLIENT, SERVER_WINS, SERVER, null);
        initAndSyncServerAndCompare(queryDelete6, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, SERVER);

        initAndSyncServerAndCompare(queryDelete6, BIDIRECTIONAL, FIRE_EVENT, SERVER, null);
    }

    @Ignore
    public void testAddDel() throws SyncException, SQLException, ContextException {
        // pass if Add and Del passes
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testModDel() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test
    public void testDelDel() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryDelete6, queryDelete6, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryDelete6, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryDelete6, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            SERVER);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryDelete6, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);


        initClientAndServerWithSyncAndCompare(queryDelete6, queryDelete6, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddUcInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryInsert7a, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryInsert7a, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testModUcInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryUpdate6b, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryUpdate6b, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelUcInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryDelete6, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryDelete6, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcAddInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryInsert7a, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryInsert7a, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testModAddInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate7b, queryInsert7a, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcModInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryUpdate6b, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryUpdate6b, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddModInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryUpdate7b, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelAddInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testModModInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryUpdate6c, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelModInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryUpdate6b, queryDelete6, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcDelInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryDelete6, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryDelete6, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDelInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryInsert7a, queryDelete7, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testModDelInvalidState() throws SyncException, SQLException, ContextException {
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSyncAndCompare(queryDelete6, queryUpdate6b, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelDelInvalidState() throws SyncException, SQLException, ContextException {
        initAndSyncClientAndCompare(queryDelete6, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, null);
        initAndSyncClientAndCompare(queryDelete6, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, null);
    }
}
