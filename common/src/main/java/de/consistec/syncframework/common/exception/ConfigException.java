package de.consistec.syncframework.common.exception;

/**
 * Exception to throw when framework configuration is broken.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 15.10.2012 14:11
 * @author Piotr Wieczorek
 * @since 0.0.1-SNAPSHOT
 */
public class ConfigException extends RuntimeException {

    /**
     *
     */
    public ConfigException() {
    }

    /**
     *
     * @param message Error message
     */
    public ConfigException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause Error cause
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause Error cause
     */
    public ConfigException(Throwable cause) {
        super(cause);
    }
}
