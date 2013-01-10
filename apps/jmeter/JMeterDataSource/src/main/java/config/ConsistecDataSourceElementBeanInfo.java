package config;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TypeEditor;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.beans.PropertyDescriptor;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: marcel
 * Date: 02.10.12
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class ConsistecDataSourceElementBeanInfo extends BeanInfoSupport {

    private static final Logger log = LoggingManager.getLoggerForClass();
    private static Map<String,Integer> TRANSACTION_ISOLATION_MAP = new HashMap<String, Integer>(5);
    static {
        // Will use default isolation
        TRANSACTION_ISOLATION_MAP.put("DEFAULT", Integer.valueOf(-1));
        TRANSACTION_ISOLATION_MAP.put("TRANSACTION_NONE", Integer.valueOf(Connection.TRANSACTION_NONE));
        TRANSACTION_ISOLATION_MAP.put("TRANSACTION_READ_COMMITTED", Integer.valueOf(Connection.TRANSACTION_READ_COMMITTED));
        TRANSACTION_ISOLATION_MAP.put("TRANSACTION_READ_UNCOMMITTED", Integer.valueOf(Connection.TRANSACTION_READ_UNCOMMITTED));
        TRANSACTION_ISOLATION_MAP.put("TRANSACTION_REPEATABLE_READ", Integer.valueOf(Connection.TRANSACTION_REPEATABLE_READ));
        TRANSACTION_ISOLATION_MAP.put("TRANSACTION_SERIALIZABLE", Integer.valueOf(Connection.TRANSACTION_SERIALIZABLE));
    }

    public ConsistecDataSourceElementBeanInfo() {
        super(ConsistecDataSourceElement.class);

        createPropertyGroup("varName", new String[] { "dataSource" });

        createPropertyGroup("pool", new String[] { "poolMax", "timeout",
                "trimInterval", "autocommit", "transactionIsolation"  });

        createPropertyGroup("keep-alive", new String[] { "keepAlive", "connectionAge", "checkQuery" });

        createPropertyGroup("database", new String[] { "dbUrl", "driver", "username", "password" });

        PropertyDescriptor p = property("dataSource");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p = property("poolMax");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "10");
        p = property("timeout");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "10000");
        p = property("trimInterval");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "60000");
        p = property("autocommit");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);
        p = property("transactionIsolation");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "DEFAULT");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        Set<String> modesSet = TRANSACTION_ISOLATION_MAP.keySet();
        String[] modes = modesSet.toArray(new String[modesSet.size()]);
        p.setValue(TAGS, modes);
        p = property("keepAlive");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, Boolean.TRUE);
        p = property("connectionAge");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "5000");
        p = property("checkQuery");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "Select 1");
        p = property("dbUrl");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p = property("driver");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p = property("username");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p = property("password", TypeEditor.PasswordEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
    }

    /**
     * @param tag
     * @return int value for String
     */
    public static int getTransactionIsolationMode(String tag) {
        if (!StringUtils.isEmpty(tag)) {
            Integer isolationMode = TRANSACTION_ISOLATION_MAP.get(tag);
            if (isolationMode == null) {
                try {
                    return Integer.parseInt(tag);
                } catch (NumberFormatException e) {
                    log.warn("Illegal transaction isolation configuration '" + tag + "'");
                }
            }
        }
        return -1;
    }
}
