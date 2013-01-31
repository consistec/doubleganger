package de.consistec.syncframework.common.data.schema;

/**
 * Supported SQL constraints.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 26.07.12 08:26
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public enum ConstraintType {

    /**
     * Simple (not composite) primary key.
     */
    PRIMARY_KEY;
    //FOREIGN_KEY for now is not supported.
}
