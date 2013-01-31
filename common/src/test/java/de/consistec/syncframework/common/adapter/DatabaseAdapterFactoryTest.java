package de.consistec.syncframework.common.adapter;

import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.TestBase;
import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;

import org.junit.Test;

/**
 * Tests database adapter factory.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 17.10.2012 09:37:17
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class DatabaseAdapterFactoryTest extends TestBase {

    /**
     * Test of newInstance method.
     * It checks if tested method returns initialized instance of configured database adapter.
     *
     * @throws DatabaseAdapterInstantiationException
     */
    @Test
    public void testNewInstance() throws DatabaseAdapterInstantiationException {
        final Config conf = Config.getInstance();
        conf.setServerDatabaseAdapter(DumbDbAdapter.class);
        IDatabaseAdapter result = DatabaseAdapterFactory.newInstance(AdapterPurpose.SERVER);
        assertEquals(DumbDbAdapter.class, result.getClass());
    }
}
