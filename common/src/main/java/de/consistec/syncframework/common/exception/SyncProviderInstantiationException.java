package de.consistec.syncframework.common.exception;

/**
 * Indicates problem with instantiation of server provider object.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 30.10.2012 12:40
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class SyncProviderInstantiationException extends Exception {

    /**
     *
     */
    public SyncProviderInstantiationException() {
    }

    /**
     *
     * @param message Error message
     */
    public SyncProviderInstantiationException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public SyncProviderInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public SyncProviderInstantiationException(Throwable cause) {
        super(cause);
    }
}
