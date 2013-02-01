package de.consistec.syncframework.client;

/*
 * #%L
 * doppelganger
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

import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Date: 26.09.12
 * Time: 17:14
 * <p/>
 * <p/>
 * JMeter Tests calls the <p>testsync.sh</p> shell script which on its part
 * calls the main method of this class.
 * The {@code Main} class calls the {@code TestSyncClient} to start a synchronization.
 * If the synchronization fails, the main method will terminate with exit status 2,
 * otherwise with exit status 0.
 *
 * @autor Marcel
 */
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class.getCanonicalName());

    private Main() {
    }

    public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException,
        ContextException {

        TestSyncClient syncClient = new TestSyncClient();

        try {
            syncClient.start(args);
        } catch (SyncException e) {
            LOGGER.error("Sync failed! ", e);
            System.exit(2);
        }
        System.exit(0);
    }
}
