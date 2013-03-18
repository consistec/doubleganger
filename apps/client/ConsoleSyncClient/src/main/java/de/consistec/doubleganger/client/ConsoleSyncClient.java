package de.consistec.doubleganger.client;

/*
 * #%L
 * Project - doppelganger
 * File - ConsoleSyncClient.java
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

import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.exception.database_adapter.DatabaseAdapterException;

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 10.07.12 Time: 14:07
 * <p/>
 *
 * @author Markus
 * @since 0.0.1-SNAPSHOT
 */
public final class ConsoleSyncClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleSyncClient.class.getCanonicalName());

    private ConsoleSyncClient() {
    }

    public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException, ContextException,
        SyncException, DatabaseAdapterException, ContextException {

        ApplicationStarter syncClient = new ApplicationStarter();

        try {
            syncClient.start(args);
        } catch (SyncException e) {
            LOGGER.error("Sync failed! ", e);
            System.exit(2);
        }
        System.exit(0);
    }
}
