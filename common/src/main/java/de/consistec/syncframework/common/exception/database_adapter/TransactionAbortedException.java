package de.consistec.syncframework.common.exception.database_adapter;

/**
 * Exception to indicate that a transaction aborted exception in database occurred.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.10.12 11:14
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class TransactionAbortedException extends DatabaseAdapterException {

    /**
     *
     */
    public TransactionAbortedException() {
    }

    /**
     *
     * @param message Error message
     */
    public TransactionAbortedException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param th Exception cause.
     */
    public TransactionAbortedException(String message, Throwable th) {
        super(message, th);
    }

    /**
     *
     * @param cause Exception cause.
     */
    public TransactionAbortedException(Throwable cause) {
        super(cause);
    }
}
