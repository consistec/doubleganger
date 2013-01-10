package de.consistec.syncframework.common;

import static de.consistec.syncframework.common.SyncDirection.CLIENT_TO_SERVER;
import static de.consistec.syncframework.common.SyncDirection.SERVER_TO_CLIENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.CLIENT_WINS;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.FIRE_EVENT;
import static de.consistec.syncframework.common.conflict.ConflictStrategy.SERVER_WINS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.consistec.syncframework.common.adapter.IDatabaseAdapter;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.server.IServerSyncProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

/**
 * Tests for Configuration class.
 *
 * @author Piotr Wieczorek
 * @company Consistec Engineering and Consulting GmbH
 * @date 15.10.2012 15:05:18
 * @since 0.0.1-SNAPSHOT
 */
public class ConfigTest extends TestBase {

    /**
     * This test prepares configuration file, loads it into configuration class and checks if all values were
     * loaded correctly.
     * It checks also if the correct default value was loaded if there were no value for option in prepared file.
     *
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws IOException
     */
    @Test
    public void testLoadFromFile() throws IllegalArgumentException, IllegalAccessException, IOException {

        File propsFile = new File("test.properties");

        try {

            // reading options names
            String serverPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_SERVER_PREFIX").get(null));
            String clientPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_CLIENT_PREFIX").get(null));
            String dbAdapterPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_DB_ADAPTER").get(null));
            String proxyProviderPrefix = String.valueOf(Whitebox.getField(Config.class, "OPTIONS_PROXY_PROVIDER").get(
                null));

            String conflictActionPrefix = String.valueOf(Whitebox.getField(Config.class,
                "OPTIONS_COMMON_CONFLICT_ACTION").get(null));

            String syncDirectionPrefix =
                String.valueOf(Whitebox.getField(Config.class, "OPTIONS_COMMON_SYNC_DIRECTION").get(null));

            String syncTablesPrefix =
                String.valueOf(Whitebox.getField(Config.class, "OPTIONS_COMMON_SYNC_TABLES").get(null));

            String nrOfApplyChangesPrefix = String.valueOf(Whitebox.getField(Config.class,
                "OPTIONS_COMMON_NR_OF_APPLY_CHANGES_TRIES_ON_TRANS_ERROR").get(null));

            String nrOfTriesPrefix = String.valueOf(Whitebox.getField(Config.class,
                "OPTIONS_COMMON_NR_OF_SYNC_TRIES_ON_TRANS_ERROR").get(null));

            String mdTabSuffixPrefix =
                String.valueOf(Whitebox.getField(Config.class, "OPTIONS_COMMON_MD_TABLE_SUFFIX").get(null));

            String servDbAdapterPrefix = String.valueOf(Whitebox.getField(Config.class,
                "OPTIONS_COMMON_SERV_DB_ADAPTER_CLASS").get(null));

            String clientDbAdapterPrefix = String.valueOf(Whitebox.getField(Config.class,
                "OPTIONS_COMMON_CLIENT_DB_ADAPTER_CLASS").get(null));

            String serverSyncProxyPrefix = String.valueOf(
                Whitebox.getField(Config.class, "OPTIONS_COMMON_SERV_PROXY").get(
                    null));

            String[] adapterOptionsNames = {"option1", "option2", "option3"};
            String[] adapterOptionsValues = {"value1", "value2", "value3"};

            Properties props = new Properties();
            Properties serverDbAdapterProps = new Properties();
            Properties clientDbAdapterProps = new Properties();
            Properties serverProviderProxyProps = new Properties();

            ConflictStrategy conflictStrategy = FIRE_EVENT;
            props.put(conflictActionPrefix, conflictStrategy.name());

            SyncDirection syncDirection = SERVER_TO_CLIENT;
            props.put(syncDirectionPrefix, syncDirection.name());

            String syncTables = "tab1,tab2,tab_";
            props.put(syncTablesPrefix, syncTables);

            int nrOfTries = 6;
            props.put(nrOfApplyChangesPrefix, String.valueOf(nrOfTries));

            int nrOfSyncTries = 4;
            props.put(nrOfTriesPrefix, String.valueOf(nrOfSyncTries));

            String mdTabSuffix = "rt2";
            props.put(mdTabSuffixPrefix, mdTabSuffix);

            Class<? extends IDatabaseAdapter> serverDbAdapter = IDatabaseAdapter.class;
            props.put(servDbAdapterPrefix, serverDbAdapter.getCanonicalName());

            Class<? extends IDatabaseAdapter> clientDbAdapter = IDatabaseAdapter.class;
            props.put(clientDbAdapterPrefix, clientDbAdapter.getCanonicalName());

            Class<? extends IServerSyncProvider> serverProxy = IServerSyncProvider.class;
            props.put(serverSyncProxyPrefix, serverProxy.getCanonicalName());

            String prefix = serverPrefix + dbAdapterPrefix;
            props.put(prefix + "." + adapterOptionsNames[0], adapterOptionsValues[0]);
            serverDbAdapterProps.put(adapterOptionsNames[0], adapterOptionsValues[0]);
            props.put(prefix + "." + adapterOptionsNames[1], adapterOptionsValues[1]);
            serverDbAdapterProps.put(adapterOptionsNames[1], adapterOptionsValues[1]);
            props.put(prefix + "." + adapterOptionsNames[2], adapterOptionsValues[2]);
            serverDbAdapterProps.put(adapterOptionsNames[2], adapterOptionsValues[2]);

            prefix = clientPrefix + dbAdapterPrefix;
            props.put(prefix + "." + adapterOptionsNames[0], adapterOptionsValues[0]);
            clientDbAdapterProps.put(adapterOptionsNames[0], adapterOptionsValues[0]);
            props.put(prefix + "." + adapterOptionsNames[1], adapterOptionsValues[1]);
            clientDbAdapterProps.put(adapterOptionsNames[1], adapterOptionsValues[1]);
            props.put(prefix + "." + adapterOptionsNames[2], adapterOptionsValues[2]);
            clientDbAdapterProps.put(adapterOptionsNames[2], adapterOptionsValues[2]);

            prefix = serverPrefix + proxyProviderPrefix;
            props.put(prefix + "." + adapterOptionsNames[0], adapterOptionsValues[0]);
            serverProviderProxyProps.put(adapterOptionsNames[0], adapterOptionsValues[0]);
            props.put(prefix + "." + adapterOptionsNames[1], adapterOptionsValues[1]);
            serverProviderProxyProps.put(adapterOptionsNames[1], adapterOptionsValues[1]);
            props.put(prefix + "." + adapterOptionsNames[2], adapterOptionsValues[2]);
            serverProviderProxyProps.put(adapterOptionsNames[2], adapterOptionsValues[2]);

            createFile(propsFile, props);

            Config instance = Config.getInstance();
            instance.loadFromFile(new FileInputStream(propsFile));

            assertEquals("Expected and loaded conflict action are different", conflictStrategy,
                instance.getConflictStrategy());
            assertEquals("Expected and loaded sync direction are different", syncDirection,
                instance.getSyncDirection());


            String[] expectedTablesNames = syncTables.split(",");
            Arrays.sort(expectedTablesNames);
            String[] resultedTablesNames = instance.getSyncTables().toArray(
                new String[instance.getSyncTables().size()]);
            Arrays.sort(resultedTablesNames);
            assertArrayEquals("Expected and loaded tables names are different", expectedTablesNames,
                resultedTablesNames);

            assertEquals("Expected and loaded \"Nr. Of. Tries\" are different", nrOfTries,
                instance.getRetryNumberOfApplyChangesOnTransactionError());
            assertEquals("Expected and loaded \"md tab suffix\" are different", mdTabSuffix,
                instance.getMdTableSuffix());
            assertEquals("Expected and loaded server db adapters are different", serverDbAdapter,
                instance.getServerDatabaseAdapter());
            assertEquals("Expected and loaded client db adapters are different", clientDbAdapter,
                instance.getClientDatabaseAdapter());
            assertEquals("Expected and loaded server proxy provider properties objects are different",
                serverProviderProxyProps, instance.getServerProxyProviderProperties());
            assertEquals("Expected and loaded server db adapter properties objects are different", serverDbAdapterProps,
                instance.getServerDatabaseProperties());
            assertEquals("Expected and loaded client db adapter properties objects are different", clientDbAdapterProps,
                instance.getClientDatabaseProperties());
            assertEquals("Expected and loaded server proxy objects are different", serverProxy,
                instance.getServerProxy());

            // removing entries which have default values
            props.remove(conflictActionPrefix);
            props.remove(nrOfApplyChangesPrefix);
            props.remove(nrOfTriesPrefix);
            props.remove(mdTabSuffixPrefix);
            props.remove(serverSyncProxyPrefix);
            props.remove(syncDirectionPrefix);
            // recreating the file
            createFile(propsFile, props);
            instance.loadFromFile(new FileInputStream(propsFile));

            // checking if default values where loaded correctly
            assertEquals("Default and loaded conflict action are different", Config.DEFAULT_CONFLICT_STRATEGY,
                instance.getConflictStrategy());
            assertEquals("Default and loaded \"Nr. Of. Tries\" are different", Config.DEFAULT_NR_SYNC_ON_TRANS_ERR,
                instance.getRetryNumberOfApplyChangesOnTransactionError());
            assertEquals("Default and loaded \"md tab suffix\" are different", Config.DEFAULT_MD_TABLE_SUFFIX,
                instance.getMdTableSuffix());
            assertEquals("Default and loaded \"sync. diirection\" are different", Config.DEFAULT_SYNC_DIRECTION,
                instance.getSyncDirection());

        } finally {
            propsFile.delete();
        }
    }

    /**
     * Tests for the following combination of sync directions and conflict strategies:
     * <p/>
     * client->server => server.wins
     * client->server => client.wins
     * client->server => fire.event
     * server->client => server.wins
     * server->client => client.wins
     * server->client => fire.event
     */


    @Test(expected = IllegalStateException.class)
    public void validateStateClientToServerAndServerWins() {
        Config configInstance = Config.getInstance();

        configInstance.setSyncDirection(CLIENT_TO_SERVER);
        configInstance.setConflictStrategy(SERVER_WINS);
    }

    @Test
    public void validateStateClientToServerAndClientWins() {
        Config configInstance = Config.getInstance();

        configInstance.setSyncDirection(CLIENT_TO_SERVER);
        configInstance.setConflictStrategy(CLIENT_WINS);

        assertTrue(configInstance.getSyncDirection() == CLIENT_TO_SERVER);
        assertTrue(configInstance.getConflictStrategy() == CLIENT_WINS);
    }

    @Test(expected = IllegalStateException.class)
    public void validateStateClientToServerAndFireEvent() {
        Config configInstance = Config.getInstance();

        configInstance.setSyncDirection(CLIENT_TO_SERVER);
        configInstance.setConflictStrategy(FIRE_EVENT);
    }

    @Test(expected = IllegalStateException.class)
    public void validateStateServerToClientAndClientWins() {
        Config configInstance = Config.getInstance();

        configInstance.setSyncDirection(SERVER_TO_CLIENT);
        configInstance.setConflictStrategy(CLIENT_WINS);
    }

    @Test(expected = IllegalStateException.class)
    public void validateStateServerToClientAndFireEvent() {
        Config configInstance = Config.getInstance();

        configInstance.setSyncDirection(SERVER_TO_CLIENT);
        configInstance.setConflictStrategy(FIRE_EVENT);
    }

    private void createFile(File propsFile, Properties props) throws IOException {

        if (propsFile.exists()) {
            propsFile.delete();
            propsFile.createNewFile();
        }
        props.store(new FileOutputStream(propsFile),
            "File generated by " + getClass().getCanonicalName() + " test class");
    }
}