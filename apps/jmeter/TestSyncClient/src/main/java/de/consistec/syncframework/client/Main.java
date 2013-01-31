package de.consistec.syncframework.client;

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
