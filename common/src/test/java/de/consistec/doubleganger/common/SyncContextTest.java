package de.consistec.doubleganger.common;

/*
 * #%L
 * Project - doubleganger
 * File - SyncContextTest.java
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.consistec.doubleganger.common.adapter.DumbDbAdapter;
import de.consistec.doubleganger.common.data.schema.Schema;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.server.IServerSyncProvider;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * Tests for synchronization context.
 *
 * @author Piotr Wieczorek
 * @company consistec Engineering and Consulting GmbH
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

            conf.setServerDatabaseAdapter(DumbDbAdapter.class);
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

    @Override
    public int applyChanges(SyncData clientData) throws SyncException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SyncData getChanges(int rev) throws SyncException {
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
}