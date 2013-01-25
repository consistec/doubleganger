package de.consistec.syncframework.server;

import static de.consistec.syncframework.common.util.CollectionsUtil.newHashSet;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.startsWith;

import de.consistec.syncframework.common.Config;
import de.consistec.syncframework.common.SyncDirection;
import de.consistec.syncframework.common.SyncSettings;
import de.consistec.syncframework.common.TableSyncStrategies;
import de.consistec.syncframework.common.TableSyncStrategy;
import de.consistec.syncframework.common.conflict.ConflictStrategy;
import de.consistec.syncframework.common.exception.SyncException;
import de.consistec.syncframework.common.i18n.Errors;
import de.consistec.syncframework.common.i18n.MessageReader;
import de.consistec.syncframework.impl.proxy.http_servlet.HttpServerSyncProxy;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author marcel
 * @company Consistec Engineering and Consulting GmbH
 * @date 25.01.13 15:40
 */
public class ServerSyncProviderValidationTest {


    private static EmbeddedSyncServiceServer server;

    private HttpServerSyncProxy proxy;


    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @BeforeClass
    public static void setUpClass() throws Exception {

        server = new EmbeddedSyncServiceServer();
        server.init();
        server.start();
    }

    /**
     * Stops the Jetty container.
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }

    @Before
    public void setUp() throws IOException, URISyntaxException {
        // initialize logging framework
        DOMConfigurator.configure(ClassLoader.getSystemResource("log4j.xml"));

        proxy = new HttpServerSyncProxy(server.getServerURI());
    }

    @Test
    public void testValidateSyncSettings() throws URISyntaxException, SyncException {

        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost");

        TableSyncStrategies serverTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);

        setServerSettings(serverTableSyncStrategies);

        Set<String> clientTables = createSyncTables("categories");
        TableSyncStrategies clientTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);
        SyncSettings clientSettings = new SyncSettings(clientTables, clientTableSyncStrategies);

        proxy.validate(clientSettings);

        // because the validate method throws exceptions if the settings are incorrect,
        // the tests is always succeeded if this assert is reached.
        assertTrue(true);
    }

    @Test
    public void testValidateSyncSettingsInvalidTables() throws SyncException {
        thrown.expect(SyncException.class);
        thrown.expectMessage(startsWith(MessageReader.read(Errors.COMMON_SYNCTABLE_SETTINGS_ERROR)));

        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost");


        TableSyncStrategies serverTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);

        setServerSettings(serverTableSyncStrategies);


        Set<String> clientTables = createSyncTables("categories", "items");
        TableSyncStrategies clientTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);
        SyncSettings clientSettings = new SyncSettings(clientTables, clientTableSyncStrategies);

        proxy.validate(clientSettings);
    }

    @Test
    public void validateSyncSettingsInvalidSyncDirection() throws
        URISyntaxException, SyncException {
        thrown.expect(SyncException.class);
        thrown.expectMessage(startsWith(MessageReader.read(Errors.COMMON_NOT_IDENTICAL_SYNCSTRATEGY)));

        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost");

        TableSyncStrategies serverTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);
        serverTableSyncStrategies.addAll(createTableSyncStrategies("items",
            SyncDirection.CLIENT_TO_SERVER, ConflictStrategy.CLIENT_WINS));

        setServerSettings(serverTableSyncStrategies);


        Set<String> clientTables = createSyncTables("categories");
        TableSyncStrategies clientTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.SERVER_TO_CLIENT, ConflictStrategy.SERVER_WINS);
        clientTableSyncStrategies.addAll(createTableSyncStrategies("items",
            SyncDirection.CLIENT_TO_SERVER, ConflictStrategy.CLIENT_WINS));

        SyncSettings clientSettings = new SyncSettings(clientTables, clientTableSyncStrategies);

        proxy.validate(clientSettings);
    }

    @Test
    public void validateSyncSettingsInvalidSyncConflictStrategy() throws
        URISyntaxException, SyncException {
        thrown.expect(SyncException.class);
        thrown.expectMessage(startsWith(MessageReader.read(Errors.COMMON_NOT_IDENTICAL_SYNCSTRATEGY)));

        Config.getInstance().getServerDatabaseProperties().setProperty("host", "localhost");

        TableSyncStrategies serverTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.SERVER_WINS);
        serverTableSyncStrategies.addAll(createTableSyncStrategies("items",
            SyncDirection.CLIENT_TO_SERVER, ConflictStrategy.CLIENT_WINS));

        setServerSettings(serverTableSyncStrategies);


        Set<String> clientTables = createSyncTables("categories");
        TableSyncStrategies clientTableSyncStrategies = createTableSyncStrategies("categories",
            SyncDirection.BIDIRECTIONAL, ConflictStrategy.FIRE_EVENT);
        clientTableSyncStrategies.addAll(createTableSyncStrategies("items",
            SyncDirection.CLIENT_TO_SERVER, ConflictStrategy.CLIENT_WINS));

        SyncSettings clientSettings = new SyncSettings(clientTables, clientTableSyncStrategies);

        proxy.validate(clientSettings);
    }

    private void setServerSettings(TableSyncStrategies serverTableSyncStrategies) {
        server.setTableSyncStrategies(serverTableSyncStrategies);
    }

    private TableSyncStrategies createTableSyncStrategies(String table, SyncDirection direction,
                                                          ConflictStrategy conflictStrategy
    ) {
        TableSyncStrategies tableSyncStrategies = new TableSyncStrategies();
        TableSyncStrategy strategy = new TableSyncStrategy(direction, conflictStrategy);
        tableSyncStrategies.addSyncStrategyForTable(table, strategy);
        return tableSyncStrategies;
    }

    private Set<String> createSyncTables(String... tableNames) {
        Set<String> tables = newHashSet();
        for (String tableName : tableNames) {
            tables.add(tableName);
        }
        return tables;
    }
}
