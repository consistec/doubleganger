package de.consistec.syncframework.common.exception;

/**
 * Indicates problems with synchronization contexts creation.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 16.11.2012 10:41:36
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class ContextException extends Exception {

    /**
     * Create exception object with default message.
     */
    public ContextException() {
        super("Can't create synchronization context");
    }

    /**
     *
     * @param message Error message
     */
    public ContextException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public ContextException(Throwable cause) {
        super(cause);
    }

}
