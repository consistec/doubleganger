package de.consistec.syncframework.client;

/*
 * #%L
 * Project - doppelganger
 * File - ApplicationStarter.java
 * %%
 * Copyright (C) 2011 - 2012 consistec GmbH
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

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncContext;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.ContextException;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.exception.database_adapter.DatabaseAdapterException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;

/**
 * Date: 10.07.12 Time: 14:19
 * <p/>
 *
 * @author Markus
 * @since 0.0.1-SNAPSHOT
 */
public class ApplicationStarter {

    private static final Logger LOGGER = Logger.getLogger(ApplicationStarter.class.getCanonicalName());
    private static final String OPTION_OUPTUT_FILE = "o";
    private static final String OPTION_SETTINGS_FILE = "s";
    private static final Config CONF = Config.getInstance();
    private Options options = new Options();

    public void start(final String[] args) throws DatabaseAdapterException, ContextException, SyncException {

        createOptions();

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args, true);

            if (line.getOptionValue(OPTION_OUPTUT_FILE) == null) {
                createConsoleLogger();
            } else {
                createFileLogger(line.getOptionValue(OPTION_OUPTUT_FILE));
            }

            InputStream in = new FileInputStream(line.getOptionValue(OPTION_SETTINGS_FILE));
            CONF.init(in);

            if (line.hasOption("--server-wins")) {
                CONF.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
            } else if (line.hasOption("--client-wins")) {
                CONF.setGlobalConflictStrategy(ConflictStrategy.CLIENT_WINS);
            } else {
                CONF.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
            }

            if (CONF.getServerProxy() == null) {
                SyncContext.local().synchronize();
            } else {
                SyncContext.client().synchronize();
            }

        } catch (ParseException pex) {
            printHelp(pex.getLocalizedMessage());
        } catch (IOException ex) {
            printHelp(ex.getLocalizedMessage());
        }
    }

    private void createOptions() {

        options.addOption(OptionBuilder
            .withLongOpt("settings")
            .withDescription("Properties file with synchronisation settings")
            .isRequired(true)
            .hasArg(true)
            .create(OPTION_SETTINGS_FILE));
        options.addOption(OptionBuilder
            .withLongOpt("server-wins")
            .withDescription("Set to let the server win on conflicts")
            .hasArg(false)
            .create());
        options.addOption(OptionBuilder
            .withLongOpt("client-wins")
            .withDescription("Set to let the client win on conflicts")
            .hasArg(false)
            .create());
        options.addOption(OptionBuilder.withLongOpt("output")
            .withDescription("Output file. If not provided, all output will be printed to console")
            .isRequired(false)
            .hasArg(true)
            .create(OPTION_OUPTUT_FILE));

    }

    private void printHelp(String msg) {

        System.out.println(msg);
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar ConsoleSyncClient.jar <options>",
            "This tool immediately starts a synchronization with the given properties.", options, "");

    }

    private void createFileLogger(String logFile) {
        try {
            SimpleLayout layout = new SimpleLayout();
            FileAppender appender = new RollingFileAppender(layout, logFile, false);
            LOGGER.addAppender(appender);
            LOGGER.setLevel(Level.DEBUG);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            LOGGER.error("Printing ERROR Statements", e);
        }
    }

    private void createConsoleLogger() {
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender appender = new ConsoleAppender(layout, "System.out");
        LOGGER.addAppender(appender);
        LOGGER.setLevel(Level.DEBUG);
    }
}
