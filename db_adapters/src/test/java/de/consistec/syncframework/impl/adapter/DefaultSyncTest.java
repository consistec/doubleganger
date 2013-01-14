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
        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, CLIENT, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, CLIENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        testInvalidState(resource);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void testInvalidState(String resource) throws SyncException, SQLException, ContextException {
        initAndSync(resource, CLIENT_TO_SERVER, SERVER_WINS, CLIENT, CLIENT);
        initAndSync(resource, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT, CLIENT);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    public void testInvalidState(String resource1, String resource2) throws SyncException, SQLException,
        ContextException {
        initClientAndServerWithSync(resource1, resource2, CLIENT_TO_SERVER, SERVER_WINS, CLIENT);
        initClientAndServerWithSync(resource1, resource2, SERVER_TO_CLIENT, CLIENT_WINS, CLIENT);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModUc() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";

        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, CLIENT, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, CLIENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testModUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        testInvalidState(resource);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelUc() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, CLIENT, CLIENT);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, CLIENT, CLIENT);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, CLIENT, CLIENT, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, CLIENT, CLIENT);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelUcInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        testInvalidState(resource);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcAdd() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, SERVER, SERVER);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, SERVER, SERVER);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, SERVER, CLIENT, SERVER);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, SERVER, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, SERVER, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        testInvalidState(resource);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testAddAdd() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        initClientAndServerAndSync(resource, BIDIRECTIONAL, SERVER_WINS,
            SERVER);
        initClientAndServerAndSync(resource, BIDIRECTIONAL, CLIENT_WINS,
            CLIENT);
        initClientAndServerAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS,
            CLIENT);

        initClientAndServerAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category7_a_insert.xml";
        testInvalidState(resource, resource);
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
        String resource2 = "category7_a_insert.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_b_update.xml";
        String resource2 = "category7_a_insert.xml";
        testInvalidState(resource1, resource2);
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
        String resource2 = "category7_a_delete.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelAddInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String resource2 = "category7_a_delete.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcMod() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, SERVER, SERVER);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, SERVER, SERVER);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, SERVER, SERVER);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, SERVER, CLIENT, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, SERVER, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcModInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_b_update.xml";
        testInvalidState(resource);
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
        String resource2 = "category7_b_update.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String resource2 = "category7_b_update.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModMod() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String resource2 = "category6_c_update.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String resource2 = "category6_c_update.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelMod() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String resource2 = "category6_a_delete.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelModInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_b_update.xml";
        String resource2 = "category6_a_delete.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testUcDel() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        initAndSync(resource, BIDIRECTIONAL, SERVER_WINS, SERVER, SERVER);
        initAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, SERVER, SERVER);
        initAndSync(resource, SERVER_TO_CLIENT, SERVER_WINS, SERVER, SERVER);
        initAndSync(resource, CLIENT_TO_SERVER, CLIENT_WINS, SERVER, CLIENT, SERVER);

        initAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, SERVER, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUcDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        testInvalidState(resource);
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
        String resource2 = "category7_a_delete.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testAddDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category7_a_insert.xml";
        String resource2 = "category7_a_delete.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testModDel() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_a_delete.xml";
        String resource2 = "category6_b_update.xml";
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, CLIENT_WINS,
            ConnectionType.CLIENT);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS,
            ConnectionType.SERVER);
        initClientAndServerWithSync(resource1, resource2, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS,
            ConnectionType.CLIENT);

        initClientAndServerWithSync(resource1, resource2, BIDIRECTIONAL, FIRE_EVENT,
            ConnectionType.SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testModDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource1 = "category6_a_delete.xml";
        String resource2 = "category6_b_update.xml";
        testInvalidState(resource1, resource2);
    }

    /**
     * @throws SyncException
     * @throws SQLException
     * @throws ContextException
     */
    @Test
    public void testDelDel() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        initClientAndServerAndSync(resource, BIDIRECTIONAL, SERVER_WINS, SERVER);
        initClientAndServerAndSync(resource, BIDIRECTIONAL, CLIENT_WINS, CLIENT);
        initClientAndServerAndSync(resource, SyncDirection.SERVER_TO_CLIENT, SERVER_WINS, SERVER);
        initClientAndServerAndSync(resource, SyncDirection.CLIENT_TO_SERVER, CLIENT_WINS, CLIENT);


        initClientAndServerAndSync(resource, BIDIRECTIONAL, FIRE_EVENT, SERVER);
    }

    @Test(expected = IllegalStateException.class)
    public void testDelDelInvalidState() throws SyncException, SQLException, ContextException {
        String resource = "category6_a_delete.xml";
        testInvalidState(resource);
    }
}
