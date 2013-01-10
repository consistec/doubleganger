package de.consistec.syncframework.common;

/**
 * Default values for md tables columns.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 29.10.12 14:37
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public final class MdTableDefaultValues {

    /**
     * Default value for <i>F</i> on client site column.
     * <p>
     * Value: {@value}
     */
    public static final int CLIENT_FLAG = 1;
    /**
     * Default value for <i>REV</i> column.
     * <p>
     * Value: {@value}
     */
    public static final int CLIENT_INIT_REVISION = -1;
    /**
     * Default value for <i>F</i> on server site column.
     * <p>
     * Value: {@value}
     */
    public static final int SERVER_FLAG = -1;

    private MdTableDefaultValues() {
        throw new AssertionError("Instance not allowed");
    }
}
