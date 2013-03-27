package de.consistec.doubleganger.client;

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

import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_DB_NAME;
import static de.consistec.doubleganger.common.adapter.impl.DatabaseAdapterConnector.PROPS_URL;

import de.consistec.doubleganger.common.Config;
import de.consistec.doubleganger.common.SyncContext;
import de.consistec.doubleganger.common.conflict.ConflictStrategy;
import de.consistec.doubleganger.common.exception.ContextException;
import de.consistec.doubleganger.common.exception.SyncException;
import de.consistec.doubleganger.common.util.StringUtil;
import de.consistec.doubleganger.impl.adapter.PostgresDatabaseAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.SimpleLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

/**
 * Date: 26.09.12 17:14
 * <p/>
 * <
 * p/>
 * The {@code TestSyncClient} represents the interface for the jmeter tests to the synchronization
 * framework. Two jmeter test categories are using this {@code TestSyncClient}, the jmeter performance tests
 * and the jmeter last tests.
 * To configure the jmeter tests two configuration files are used during the synchronization:
 * <ul>
 * <li>config_posgre.properties file for jmeter last tests and</li>
 * <li>performance_config_postgre.properties file for jmeter performance tests</li>
 * </ul>
 * .
 * <p/>
 * The JMeter tests passes special arguments to configure the
 * synchronization framework. This arguments with its options are listed below:
 * <p/>
 * <ul>
 * <li>s - server properties file name</li>
 * <li>m - thread number which calls this process</li>
 * <li>o - log file name</li>
 * <li>t - number of retries to apply changes</li>
 * <li>r - number to retry the whole sync</li>
 * <p/>
 * </ul>
 * <p/>
 * Passed arguments which are also present as properties in one of the configuration properties files
 * have a higher priority which means that the properties in configuration files are overridden.
 * <p/>
 * For example:
 * <p>In the configration file are following properties configured:</p>
 * framework.server.number_of_get_changes_tries_on_transaction_error=3 and
 * framework.client.number_of_sync_tries_on_transaction_error=3
 * <p/>
 * <p>Ppassed arguments are as follows:</p>
 * -t 5 and -r 7
 * <p/>
 * then the values of the passed arguments 5 and 7 will be used during the synchronization.
 *
 * @author Marcel
 * @since 0.0.1-SNAPSHOT
 */
public class TestSyncClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestSyncClient.class.getCanonicalName());
    // obtaining frameworks configuration object
    private static final Config CONF = Config.getInstance();
    private Options options = new Options();
    private String outFile;
    private boolean retrySync = false;

    public void start(String[] args) throws SyncException, IOException, ParseException, ClassNotFoundException,
        ContextException {

        createOptions();

        CommandLineParser parser = new GnuParser();
        try {
            CommandLine line = parser.parse(options, args, true);
            outFile = line.getOptionValue("o", null);
            prepareLogger();

            int clientThreadNumber = Integer.valueOf(line.getOptionValue("m")).intValue();

            CONF.init(new FileInputStream(line.getOptionValue("s")));

            LOGGER.debug("Client db adapter: {}", CONF.getClientDatabaseAdapter());
            LOGGER.debug("Client db adapter properties {}", CONF.getClientDatabaseProperties().entrySet());
            LOGGER.debug("Server db adapter: {}", CONF.getServerDatabaseAdapter());

            String maxApplyChangesRepeat = line.getOptionValue("t");
            if (!StringUtil.isNullOrEmpty(maxApplyChangesRepeat)) {
                CONF.setRetryNumberOfApplyChangesOnTransactionError(Integer.parseInt(maxApplyChangesRepeat));
                LOGGER.info(
                    "RepeatNumberOfSyncOnTransactionError set to {}",
                    Integer.valueOf(CONF.getRetryNumberOfApplyChangesOnTransactionError()));
            }

            String maxSyncRepeat = line.getOptionValue("r");
            if (!StringUtil.isNullOrEmpty(maxSyncRepeat)) {
                CONF.setSyncRetryNumber(Integer.parseInt(maxSyncRepeat));
                LOGGER.info("SyncRetryNumber set to {}", Integer.valueOf(CONF.getSyncRetryNumber()));
            }

            CONF.setGlobalConflictStrategy(ConflictStrategy.SERVER_WINS);
            // initial sync
            sync(clientThreadNumber);

        } catch (ParseException e) {
            printHelp(e);
        }
    }

    private void sync(int clientThreadNumber) throws SyncException, IOException, ContextException {

        if (!retrySync) {
            Properties clientDbProps = CONF.getClientDatabaseProperties();
            String oldClientDbUrl = clientDbProps.getProperty(PROPS_URL);
            if (!StringUtil.isNullOrEmpty(oldClientDbUrl)) {

                clientDbProps.put(PROPS_URL, oldClientDbUrl + "_" + clientThreadNumber);
                CONF.setClientDatabaseProperties(clientDbProps);

            } else {

                // if using PostgreSQL adapter
                if (PostgresDatabaseAdapter.class.equals(CONF.getClientDatabaseAdapter())) {
                    String oldDbName = clientDbProps.getProperty(PROPS_DB_NAME);
                    clientDbProps.put(PROPS_DB_NAME, oldDbName + "_" + clientThreadNumber);
                }

            }
        }

        if (CONF.getServerProxy() == null) {
            SyncContext.local().synchronize();
        } else {
            SyncContext.client().synchronize();
        }
    }

    private void createOptions() {
        options.addOption(OptionBuilder
            .withLongOpt("settings")
            .withDescription("Properties file with settings for server connection")
            .isRequired()
            .hasArg(true)
            .create("s"));
        options.addOption(OptionBuilder
            .withLongOpt("output")
            .withDescription("Write program output to file")
            .hasArg(true)
            .create("o"));
        options.addOption(OptionBuilder.withLongOpt("number of current user")
            .withDescription("gives the number of user (current jmeter-thread) who call this process")
            .isRequired(false)
            .hasArg(true)
            .create("m"));
        options.addOption(OptionBuilder.withLongOpt("number of apply changes tries")
            .withDescription("gives the repeating number of apply changes if a transaction error occurs.")
            .isRequired(false)
            .hasArg(true)
            .create("t"));
        options.addOption(OptionBuilder.withLongOpt("number of sync tries")
            .withDescription("gives the repeating number of syncs if client revision != server revision.")
            .isRequired(false)
            .hasArg(true)
            .create("r"));

    }

    private void printHelp(Exception e) {
        System.out.println(e.getMessage());
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("java -jar TestSyncClient.jar <options>",
            "This tool immediately starts a synchronization with the given properties.", options, "");
    }

    /**
     * Prepares files for logger (if outFile isn't null). If outFile is null, log messages will be printed to System.out
     * To file name (before it's extension) timestamp will be appended.
     *
     * @throws IOException well, sheet happens
     */
    private void prepareLogger() throws IOException {

        if (outFile != null) {

            File logFilePath = new File(outFile);

            String fileName = logFilePath.getName();
            String extension = FileUtil.getExtension(fileName);

            StringBuilder builder = new StringBuilder(logFilePath.getParent().toString());
            builder.append("/");
            builder.append(fileName.substring(0, fileName.length() - (extension.length() + 1)));
            builder.append("_");
            builder.append(System.currentTimeMillis());
            builder.append(".");
            builder.append(extension);
            logFilePath = new File(builder.toString());
            createFileLogger(logFilePath.toString());

        } else {
            createConsoleLogger();
        }
    }

    private void createFileLogger(String logFile) {
        try {
            SimpleLayout layout = new SimpleLayout();
            FileAppender appender = new RollingFileAppender(layout, logFile, false);
            appender.setThreshold(Level.ALL);
            BasicConfigurator.configure(appender);
        } catch (IOException e) {
            LOGGER.error("Printing ERROR Statements", e);
        }
    }

    private void createConsoleLogger() {
        SimpleLayout layout = new SimpleLayout();
        ConsoleAppender appender = new ConsoleAppender(layout, "System.out");
        appender.setThreshold(Level.ALL);
        BasicConfigurator.configure(appender);
    }
}
