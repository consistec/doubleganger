package de.consistec.syncframework.common.exception.database_adapter;

/**
 * Exception to indicate a problem during database adapter instantiation.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.07.12 10:33
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 * <p/>
 */
public class DatabaseAdapterInstantiationException extends DatabaseAdapterException {

    /**
     *
     */
    public DatabaseAdapterInstantiationException() {
        super();
    }

    /**
     *
     * @param message Error message
     */
    public DatabaseAdapterInstantiationException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public DatabaseAdapterInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public DatabaseAdapterInstantiationException(Throwable cause) {
        super(cause);
    }
}
