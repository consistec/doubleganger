package de.consistec.syncframework.common.exception;

/**
 * This exception indicates errors with serialization/deserialization of data to be send/receive
 * from remote server provider.
 *
 * @company Consistec Engineering and Consulting GmbH
 * @date 12.04.12 10:21
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 *
 */
public class SerializationException extends Exception {

    /**
     *
     * @param message Error message
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     *
     */
    public SerializationException() {
        super();
    }

    /**
     *
     * @param message Error message
     * @param th Error cause
     */
    public SerializationException(String message, Throwable th) {
        super(message, th);
    }

    /**
     *
     * @param th Error cause
     */
    public SerializationException(Throwable th) {
        super(th);
    }
}
