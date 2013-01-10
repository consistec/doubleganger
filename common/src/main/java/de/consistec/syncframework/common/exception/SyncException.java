package de.consistec.syncframework.common.exception;

/**
 * This exception indicates general problem with synchronization.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date unknown
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SyncException extends Exception {

//    private int status; moved to ServerStatusException class

    /**
     * Instantiates a new sync exception.
     */
    public SyncException() {
        super();
    }

    /**
     * Instantiates a new sync exception.
     *
     * @param message the message
     */
    public SyncException(String message) {
        super(message);
    }

    /**
     * Instantiates a new sync exception.
     *
     * @param status
     * @param message the message
     */
//    public SyncException(int status, String message) {
//        super(message);
//
//        this.status = status;
//    }

    /**
     * Instantiates a new sync exception.
     *
     * @param message the message
     * @param th the throwable
     */
    public SyncException(String message, Throwable th) {
        super(message, th);
    }

    /**
     * Instantiates a new sync exception.
     * <p/>
     * @param cause the cause
     */
    public SyncException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @return
     */
//    public int getStatus() {
//        return this.status;
//    }
}
