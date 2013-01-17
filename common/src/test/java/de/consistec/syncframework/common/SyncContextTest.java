package de.consistec.syncframework.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.adapter.DumpDbAdapter;
import de.consistec.syncframework.common.data.Change;
import de.consistec.syncframework.common.data.schema.Schema;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.server.IServerSyncProvider;

import java.util.List;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * Tests for synchronization context.
 *
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 31.10.2012 12:39:37
 * @since 0.0.1-SNAPSHOT
 */
public class SyncContextTest extends TestBase implements IServerSyncProvider {

    /**
     * Test of newInstance method.
     * <p>
     * It checks if tested method returns correct server provider class.
     * Check is made with "server proxy class" option configured and also when this options returns null.
     * In first case, tested method should return initialized instance of configured proxy class,
     * and in the second case it should thrown {@link IllegalStateException}.
     * </p>
     */
    @Test
    public void testProxyNewInstance() throws Exception {

        Class<?> resultClass = null;
        try {
            final Config conf = Config.getInstance();

            conf.setServerDatabaseAdapter(DumpDbAdapter.class);
            conf.setServerProxy(null);

            Class<Object> innerClass = Whitebox.getInnerClassType(SyncContext.class, "ServerProxyFactory");
            IllegalStateException stateEx = null;
            try {
                Whitebox.invokeMethod(innerClass, "newInstance", new Object[0]).getClass();
            } catch (IllegalStateException ex) {
                stateEx = ex;
            }

            // should return default provider, no proxy.
            assertNotNull("When no server proxy is configured, IllegalStateException should be thrown", stateEx);

            // setting custom proxy class
            conf.setServerProxy(getClass());

            resultClass = Whitebox.invokeMethod(innerClass, "newInstance", new Object[0]).getClass();
        } catch (Exception e) {
            LOGGER.error(
                e.getLocalizedMessage());  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
        assertTrue(
            "Returned Server Proxy implementation is different then the specified in framework configuration class",
            getClass().equals(resultClass));
    }

    //<editor-fold defaultstate="collapsed" desc="Methods from IServerSyncProvider interface" >
    @Override
    public int applyChanges(List<Change> changes, int clientRevision) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Tuple<Integer, List<Change>> getChanges(int rev) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Schema getSchema() throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void validate(final SyncSettings clientSettings) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    //</editor-fold>
}