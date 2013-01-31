package de.consistec.syncframework.common.exception;

/**
 * Exception thrown during conversion of {@link de.consistec.syncframework.common.data.schema.Schema} objects.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 27.07.12 10:02
 * @author Markus Backes
 * @since 0.0.1-SNAPSHOT
 */
public class SchemaConverterException extends Exception {

    /**
     *
     */
    public SchemaConverterException() {
        super();
    }

    /**
     *
     * @param message Error message
     */
    public SchemaConverterException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause The cause of exception
     */
    public SchemaConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param cause The cause of exception
     */
    public SchemaConverterException(Throwable cause) {
        super(cause);
    }
}
