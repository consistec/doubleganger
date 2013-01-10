package de.consistec.syncframework.common.exception.database_adapter;

/**
 * This exception indicates a general problem within the database adapter.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 04.07.12 08:51
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 *
 */
public class DatabaseAdapterException extends Exception {

    /**
     * Creates new object without message.
     */
    public DatabaseAdapterException() {
    }

    /**
     * Creates new object with a message.
     *
     * @param message Message describing what happend.
     */
    public DatabaseAdapterException(String message) {
        super(message);
    }

    /**
     * Creates new object with message and cause exception.
     *
     * @param message Message describing what happend.
     * @param cause Error cause
     */
    public DatabaseAdapterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates new object with cause exception.
     * @param cause Error cause
     */
    public DatabaseAdapterException(Throwable cause) {
        super(cause);
    }
}
