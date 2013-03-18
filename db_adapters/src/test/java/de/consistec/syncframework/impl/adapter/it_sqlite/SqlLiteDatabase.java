package de.consistec.syncframework.impl.adapter.it_sqlite;

/*
 * #%L
 * Project - doppelganger
 * File - SqlLiteDatabase.java
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

import de.consistec.syncframework.common.adapter.DatabaseAdapterFactory;
import de.consistec.syncframework.impl.TestDatabase;
import de.consistec.syncframework.impl.adapter.DummyDataSource;

/**
 * @author marcel
 * @company consistec Engineering and Consulting GmbH
 * @date 30.01.13 15:14
 */
public class SqlLiteDatabase extends TestDatabase {

    private static final String CONFIG_FILE = "/config_sqlite.properties";

    public SqlLiteDatabase(DatabaseAdapterFactory.AdapterPurpose side) {
        super(CONFIG_FILE, DummyDataSource.SupportedDatabases.SQLITE, side);
    }
}
