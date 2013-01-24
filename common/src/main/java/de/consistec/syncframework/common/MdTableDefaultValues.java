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
     * Default value for the <i>F</i> column on the client.
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
     * Value for the <i>F</i> column on the server when the triggers are activated and a row has been deleted.
     * <p>
     * Value: {@value}
     */
    public static final int FLAG_DELETED = -1;
    /**
     * Default value for the <i>F</i> column on the server when the triggers are activated.
     * <p>
     * Value: {@value}
     */
    public static final int FLAG_PROCESSED = 0;
    /**
     * Value for the <i>F</i> column on the server when the triggers are activated and a row has been modified.
     * <p>
     * Value: {@value}
     */
    public static final int FLAG_MODIFIED = 1;
    /**
     * Value for the <i>F</i> column on the server when the triggers are activated and a row has been inserted.
     * <p>
     * Value: {@value}
     */
    public static final int FLAG_INSERTED = 2;
    /**
     * Value for the <i>MDV</i> column on the server when the triggers are activated and a row has been deleted.
     * <p>
     * Value: {@value}
     */
    public static final String MDV_DELETED_VALUE = null;
    /**
     * Value for the <i>MDV</i> column on the server when the triggers are activated and a row has been modified.
     * <p>
     * Value: {@value}
     */
    public static final String MDV_MODIFIED_VALUE = "";

    /**
     * Value for the <i>MDV</i> column on the server when the triggers are activated and a row has been modified.
     * <p>
     * Value: {@value}
     */
    public static final int METADATA_COLUMN_COUNT = 4;

    /**
     * Name of the <i>MDV</i> column in the metadata table, containing the HASH value of the matching data row.
     * <p>
     * Value: {@value}
     */
    public static final String MDV_COLUMN_NAME = "mdv";
    /**
     * Name of the <i>F</i> column in the metadata table.
     * <p>
     * Value: {@value}
     */
    public static final String FLAG_COLUMN_NAME = "f";
    /**
     * Name of the <i>REV</i> column in the metadata table.
     * <p>
     * Value: {@value}
     */
    public static final String REV_COLUMN_NAME = "rev";
    /**
     * Name of the <i>PK</i> column in the metadata table.
     * <p>
     * Value: {@value}
     */
    public static final String PK_COLUMN_NAME = "pk";

    private MdTableDefaultValues() {
        throw new AssertionError("Instance not allowed");
    }
}
