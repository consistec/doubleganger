package de.consistec.syncframework.impl.adapter.it_sqlite;

/*
 * #%L
 * Project - doppelganger
 * File - ITSyncSqlite.java
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

import static de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose.CLIENT;
import static de.consistec.syncframework.common.adapter.DatabaseAdapterFactory.AdapterPurpose.SERVER;

import de.consistec.syncframework.impl.SynchronizationIT;
import de.consistec.syncframework.impl.TestScenario;

/**
 * @author davidm
 * @company consistec Engineering and Consulting GmbH
 * @date 15.01.2013 10:24:40
 */
public class ITSyncSqlite extends SynchronizationIT {

    public ITSyncSqlite(TestScenario scenario) {
        super(scenario);
        clientDb = new SqlLiteDatabase(CLIENT);
        serverDb = new SqlLiteDatabase(SERVER);
    }
}
