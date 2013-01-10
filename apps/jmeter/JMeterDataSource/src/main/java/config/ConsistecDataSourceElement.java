package config;

import org.apache.avalon.excalibur.datasource.DataSourceComponent;
import org.apache.avalon.excalibur.datasource.ResourceLimitingJdbcDataSource;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 02.10.12
 * Time: 10:37
 * To change this template use File | Settings | File Templates.
 */
public class ConsistecDataSourceElement extends AbstractTestElement
        implements ConfigElement, TestStateListener, TestBean
{
    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 233L;
    public static final String POOLING_PER_THREAD_NOT_SUPPORTED_MSG = "MaxPool has to be greater than 0. Pooling per thread isn't supported!";

    private transient String dataSource, driver, dbUrl, username, password, checkQuery, poolMax, connectionAge, timeout,
            trimInterval, transactionIsolation;

    private transient boolean keepAlive, autocommit;

    /*
     *  The datasource is set up by testStarted and cleared by testEnded.
     *  These are called from different threads, so access must be synchronized.
     *  The same instance is called in each case.
    */
    private transient ResourceLimitingJdbcDataSource excaliburSource;


    public ConsistecDataSourceElement() {
    }

    public void testEnded() {
        synchronized (this) {
            JMeterVariables variables = getThreadContext().getVariables();
            String threadsToSample = variables.get("users");
            int sampleThreadNumber = Integer.valueOf(threadsToSample).intValue();
            for(int i = 1; i <= sampleThreadNumber; i++)
            {
                String poolName = StringUtils.replace(getDataSource(), "StandardJMeterEngine", Integer.valueOf(i).toString());
                Object dscObject = variables.getObject(poolName);
                if(dscObject != null)
                {
                    DataSourceComponentImpl dataSourceComponent = (DataSourceComponentImpl)dscObject;
                    dataSourceComponent.close();
                }
            }

            excaliburSource = null;
        }
    }

    public void testEnded(String host) {
        testEnded();
    }

    public void testIterationStart(LoopIterationEvent event) {
    }

    public void testStarted(String host) {
        testStarted();
    }

    @SuppressWarnings("deprecation") // call to TestBeanHelper.prepare() is intentional
    public void testStarted() {
        log.info("Test startet for thread " + getThreadContext().getThreadNum());
        this.setRunningVersion(true);
        TestBeanHelper.prepare(this);
        JMeterVariables variables = getThreadContext().getVariables();
        String threadsToSample = variables.get("users");
        if(StringUtils.isEmpty(threadsToSample))
        {
            throw new IllegalStateException("Can not find number of threads to sample! Please define the variable 'users'.");
        }

        int sampleThreadNumber = Integer.valueOf(threadsToSample).intValue();
        for(int i = 1; i <= sampleThreadNumber; i++)
        {
            String poolName = fixDatasourceSubstitution(i);

            if (variables.getObject(poolName) != null) {
                log.error("JDBC data source already defined for: "+poolName);
            } else {
                String maxPool = getPoolMax();
                if (maxPool.equals("0")){ // i.e. if we want per thread pooling
                    log.error(POOLING_PER_THREAD_NOT_SUPPORTED_MSG);
                } else {
                    ResourceLimitingJdbcDataSource src=initPool(maxPool, i);

                    synchronized(this){
                        excaliburSource = src;
                        log.info("save " + poolName + " to thread context");
                        variables.putObject(poolName, new DataSourceComponentImpl(excaliburSource));
                    }
                }
            }
        }
    }

    private String fixDatasourceSubstitution(int threadNumber)
    {
        return StringUtils.replace(getDataSource(), "StandardJMeterEngine", Integer.valueOf(threadNumber).toString());
    }

    @Override
    public Object clone() {
        ConsistecDataSourceElement el = (ConsistecDataSourceElement) super.clone();
        el.excaliburSource = excaliburSource;
        return el;
    }

    /*
     * Utility routine to get the connection from the pool.
     * Purpose:
     * - allows JDBCSampler to be entirely independent of the pooling classes
     * - allows the pool storage mechanism to be changed if necessary
     */
    public static Connection getConnection(String poolName) throws SQLException {
        DataSourceComponent pool = (DataSourceComponent)
                JMeterContextService.getContext().getVariables().getObject(poolName);

        if (pool == null) {
            throw new SQLException("No pool found named: '" + poolName + "'");
        }
        return pool.getConnection();
    }

    /*
     * Set up the DataSource - maxPool is a parameter, so the same code can
     * also be used for setting up the per-thread pools.
    */
    private ResourceLimitingJdbcDataSource initPool(String maxPool, int threadNumber) {
        ResourceLimitingJdbcDataSource source = null;
        source = new ResourceLimitingJdbcDataSource();
        DefaultConfiguration config = new DefaultConfiguration("rl-jdbc"); // $NON-NLS-1$

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("MaxPool: ");
            sb.append(maxPool);
            sb.append(" Timeout: ");
            sb.append(getTimeout());
            sb.append(" TrimInt: ");
            sb.append(getTrimInterval());
            sb.append(" Auto-Commit: ");
            sb.append(isAutocommit());
            log.debug(sb.toString());
        }
        DefaultConfiguration poolController = new DefaultConfiguration("pool-controller"); // $NON-NLS-1$
        poolController.setAttribute("max", maxPool); // $NON-NLS-1$
        poolController.setAttribute("max-strict", "true"); // $NON-NLS-1$ $NON-NLS-2$
        poolController.setAttribute("blocking", "true"); // $NON-NLS-1$ $NON-NLS-2$
        poolController.setAttribute("timeout", getTimeout()); // $NON-NLS-1$
        poolController.setAttribute("trim-interval", getTrimInterval()); // $NON-NLS-1$
        config.addChild(poolController);

        DefaultConfiguration autoCommit = new DefaultConfiguration("auto-commit"); // $NON-NLS-1$
        autoCommit.setValue(String.valueOf(isAutocommit()));
        config.addChild(autoCommit);

        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("KeepAlive: ");
            sb.append(isKeepAlive());
            sb.append(" Age: ");
            sb.append(getConnectionAge());
            sb.append(" CheckQuery: ");
            sb.append(getCheckQuery());
            log.debug(sb.toString());
        }
        DefaultConfiguration cfgKeepAlive = new DefaultConfiguration("keep-alive"); // $NON-NLS-1$
        cfgKeepAlive.setAttribute("disable", String.valueOf(!isKeepAlive())); // $NON-NLS-1$
        cfgKeepAlive.setAttribute("age", getConnectionAge()); // $NON-NLS-1$
        cfgKeepAlive.setValue(getCheckQuery());
        poolController.addChild(cfgKeepAlive);

        String _username = getUsername();
        if (log.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder(40);
            sb.append("Driver: ");
            sb.append(getDriver());
            sb.append(" DbUrl: ");
            sb.append(getDbUrl());
            sb.append(" User: ");
            sb.append(_username);
            log.debug(sb.toString());
        }
        DefaultConfiguration cfgDriver = new DefaultConfiguration("driver"); // $NON-NLS-1$
        cfgDriver.setValue(getDriver());
        config.addChild(cfgDriver);
        DefaultConfiguration cfgDbUrl = new DefaultConfiguration("dburl"); // $NON-NLS-1$
        cfgDbUrl.setValue(getDbUrl());


        if(log.isDebugEnabled())
        {
            log.debug("change db-url: " + getDbUrl());
        }

        String url = StringUtils.replace(getDbUrl(), "StandardJMeterEngine", Integer.valueOf(threadNumber).toString());

        log.info(String.format("set db-url %s to db config", url));
        cfgDbUrl.setValue(url);

        config.addChild(cfgDbUrl);

        if (_username.length() > 0){
            DefaultConfiguration cfgUsername = new DefaultConfiguration("user"); // $NON-NLS-1$
            cfgUsername.setValue(_username);
            config.addChild(cfgUsername);
            DefaultConfiguration cfgPassword = new DefaultConfiguration("password"); // $NON-NLS-1$
            cfgPassword.setValue(getPassword());
            config.addChild(cfgPassword);
        }

        // log is required to ensure errors are available
        source.enableLogging(new LogKitLogger(log));
        try {
            source.configure(config);
            source.setInstrumentableName(getDataSource());
        } catch (org.apache.avalon.framework.configuration.ConfigurationException e) {
            log.error("Could not configure datasource for pool: " + getDataSource(), e);
        }
        return source;
    }

    /*
     * Wrapper class to allow getConnection() to be implemented for both shared
     * and per-thread pools.
     *
     */
    private class DataSourceComponentImpl implements DataSourceComponent{

        private final ResourceLimitingJdbcDataSource sharedDSC;

        DataSourceComponentImpl(ResourceLimitingJdbcDataSource p_dsc){
            sharedDSC=p_dsc;
        }

        public Connection getConnection() throws SQLException {
            Connection conn = null;
            ResourceLimitingJdbcDataSource dsc = null;
            if (sharedDSC != null){ // i.e. shared pool
                dsc = sharedDSC;
            } else {
                log.error(POOLING_PER_THREAD_NOT_SUPPORTED_MSG);
            }
            if (dsc != null) {
                conn=dsc.getConnection();
                int transactionIsolation = ConsistecDataSourceElementBeanInfo.getTransactionIsolationMode(getTransactionIsolation());
                if (transactionIsolation >= 0 && conn.getTransactionIsolation() != transactionIsolation) {
                    try {
                        // make sure setting the new isolation mode is done in an auto committed transaction
                        conn.setTransactionIsolation(transactionIsolation);
                        log.debug("Setting transaction isolation: " + transactionIsolation + " @"
                                + System.identityHashCode(dsc));
                    } catch (SQLException ex) {
                        log.error("Could not set transaction isolation: " + transactionIsolation + " @"
                                + System.identityHashCode(dsc));
                    }
                }
            }

            if(log.isDebugEnabled())
            {
                log.info(String.format("get connection to %s", conn.getCatalog()));
            }
            return conn;
        }

        public void configure(Configuration arg0) throws ConfigurationException {
        }

        public void close()
        {
            if(sharedDSC != null)
            {
                sharedDSC.dispose();
            }
        }
    }

    public void addConfigElement(ConfigElement config) {
    }

    public boolean expectsModification() {
        return false;
    }

    /**
     * @return Returns the checkQuery.
     */
    public String getCheckQuery() {
        return checkQuery;
    }

    /**
     * @param checkQuery
     *            The checkQuery to set.
     */
    public void setCheckQuery(String checkQuery) {
        this.checkQuery = checkQuery;
    }

    /**
     * @return Returns the connectionAge.
     */
    public String getConnectionAge() {
        return connectionAge;
    }

    /**
     * @param connectionAge
     *            The connectionAge to set.
     */
    public void setConnectionAge(String connectionAge) {
        this.connectionAge = connectionAge;
    }

    /**
     * @return Returns the poolname.
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource
     *            The poolname to set.
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @return Returns the dbUrl.
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * @param dbUrl
     *            The dbUrl to set.
     */
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * @return Returns the driver.
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver
     *            The driver to set.
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the poolMax.
     */
    public String getPoolMax() {
        return poolMax;
    }

    /**
     * @param poolMax
     *            The poolMax to set.
     */
    public void setPoolMax(String poolMax) {
        this.poolMax = poolMax;
    }

    /**
     * @return Returns the timeout.
     */
    public String getTimeout() {
        return timeout;
    }

    /**
     * @param timeout
     *            The timeout to set.
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    /**
     * @return Returns the trimInterval.
     */
    public String getTrimInterval() {
        return trimInterval;
    }

    /**
     * @param trimInterval
     *            The trimInterval to set.
     */
    public void setTrimInterval(String trimInterval) {
        this.trimInterval = trimInterval;
    }

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return Returns the autocommit.
     */
    public boolean isAutocommit() {
        return autocommit;
    }

    /**
     * @param autocommit
     *            The autocommit to set.
     */
    public void setAutocommit(boolean autocommit) {
        this.autocommit = autocommit;
    }

    /**
     * @return Returns the keepAlive.
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * @param keepAlive
     *            The keepAlive to set.
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * @return the transaction isolation level
     */
    public String getTransactionIsolation() {
        return transactionIsolation;
    }

    /**
     * @param transactionIsolation The transaction isolation level to set. <code>NULL</code> to
     * use the default of the driver.
     */
    public void setTransactionIsolation(String transactionIsolation) {
        this.transactionIsolation = transactionIsolation;
    }
}
