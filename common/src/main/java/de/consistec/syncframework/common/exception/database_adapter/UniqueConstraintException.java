package de.consistec.syncframework.common.exception.database_adapter;

/**
 * Exception to indicate that unique constraint voliation in database occurred.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 27.11.12 13:37
 * @author marcel
 */
public class UniqueConstraintException extends DatabaseAdapterException {

    /**
     * Creates new object without message.
     */
    public UniqueConstraintException() {
    }

    /**
     * Creates new object with a message.
     * @param message Message describing what happend.
     */
    public UniqueConstraintException(String message) {
        super(message);
    }

    /**
     * Creates new object with a message and cause exception.
     * @param message Message describing what happend.
     * @param cause Cause exception.
     */
    public UniqueConstraintException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates new object with cause exception.
     * @param cause Cause exception.
     */
    public UniqueConstraintException(Throwable cause) {
        super(cause);
    }



}
