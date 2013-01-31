package de.consistec.syncframework.common.exception;

/*
 * #%L
 * Project - doppelganger
 * File - ExceptionsTest.java
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

import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterInstantiationException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;
import static org.junit.Assert.assertEquals;

import de.consistec.syncframework.common.TestBase;

import org.junit.Test;

/**
 * Tests for frameworks Exceptions objects.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 28.06.12 14:15
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class ExceptionsTest extends TestBase {

    private static final String TEST_MESSAGE = "Testmessage";
    private static final String TEST_MESSAGE2 = "Testmessage2";
    private static final Throwable TEST_THROWABLE = new Throwable(TEST_MESSAGE2);

    @Test
    public void testDefaultExceptions() {
        SerializationException sex = new SerializationException();
        assertEquals(null, sex.getLocalizedMessage());

        DatabaseAdapterException dex = new DatabaseAdapterException();
        assertEquals(null, dex.getLocalizedMessage());

        DatabaseAdapterInstantiationException diex = new DatabaseAdapterInstantiationException();
        assertEquals(null, diex.getLocalizedMessage());

        ConfigException cerr = new ConfigException();
        assertEquals(null, cerr.getLocalizedMessage());

        SyncException ex = new SyncException();
        assertEquals(null, ex.getLocalizedMessage());

        SchemaConverterException scex = new SchemaConverterException();
        assertEquals(null, scex.getLocalizedMessage());
    }

    @Test
    public void testExceptionsWithMessage() {
        SerializationException sex = new SerializationException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, sex.getLocalizedMessage());

        DatabaseAdapterException dex = new DatabaseAdapterException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, dex.getLocalizedMessage());

        DatabaseAdapterInstantiationException diex = new DatabaseAdapterInstantiationException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, diex.getLocalizedMessage());

        ConfigException cerr = new ConfigException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, cerr.getLocalizedMessage());

        SyncException ex = new SyncException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, ex.getLocalizedMessage());

        SchemaConverterException scex = new SchemaConverterException(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE, scex.getLocalizedMessage());
    }

    @Test
    public void testExceptionsWithMessageAndThrowable() {
        SerializationException sex = new SerializationException(TEST_MESSAGE, TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, sex.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, sex.getCause().getLocalizedMessage());

        DatabaseAdapterException dex = new DatabaseAdapterException(TEST_MESSAGE, TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, dex.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, dex.getCause().getLocalizedMessage());

        DatabaseAdapterInstantiationException diex = new DatabaseAdapterInstantiationException(TEST_MESSAGE,
            TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, diex.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, diex.getCause().getLocalizedMessage());

        SyncException ex = new SyncException(TEST_MESSAGE, TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, ex.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, ex.getCause().getLocalizedMessage());

        SchemaConverterException scex = new SchemaConverterException(TEST_MESSAGE, TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, scex.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, scex.getCause().getLocalizedMessage());

        ConfigException cerr = new ConfigException(TEST_MESSAGE, TEST_THROWABLE);
        assertEquals(TEST_MESSAGE, cerr.getLocalizedMessage());
        assertEquals(TEST_MESSAGE2, cerr.getCause().getLocalizedMessage());
    }

    @Test
    public void testExceptionsWithThrowable() {
        SerializationException sex = new SerializationException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, sex.getCause().getLocalizedMessage());

        DatabaseAdapterException dex = new DatabaseAdapterException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, dex.getCause().getLocalizedMessage());

        DatabaseAdapterInstantiationException diex = new DatabaseAdapterInstantiationException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, diex.getCause().getLocalizedMessage());

        SyncException ex = new SyncException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, ex.getCause().getLocalizedMessage());

        SchemaConverterException scex = new SchemaConverterException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, scex.getCause().getLocalizedMessage());

        ConfigException cerr = new ConfigException(TEST_THROWABLE);
        assertEquals(TEST_MESSAGE2, cerr.getCause().getLocalizedMessage());

    }
}
