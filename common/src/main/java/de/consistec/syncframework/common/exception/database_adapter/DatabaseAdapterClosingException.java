package de.consistec.syncframework.common.exception.database_adapter;

/**
 * Exception that indicates problem with closing database adapter resources (mainly the connection).
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 24.10.12 13:36
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class DatabaseAdapterClosingException extends DatabaseAdapterException {

    /**
     *
     */
    public DatabaseAdapterClosingException() {
    }

    /**
     *
     * @param message Error message
     */
    public DatabaseAdapterClosingException(String message) {
        super(message);
    }

    /**
     *
     * @param msg Error message
     * @param th Error cause
     */
    public DatabaseAdapterClosingException(String msg, Throwable th) {
        super(msg, th);
    }

    /**
     *
     * @param cause Error cause
     */
    public DatabaseAdapterClosingException(Throwable cause) {
        super(cause);
    }

}
