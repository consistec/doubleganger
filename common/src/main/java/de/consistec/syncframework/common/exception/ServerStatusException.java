package de.consistec.syncframework.common.exception;

import de.consistec.syncframework.common.server.ServerStatus;

/**
 * This exception indicates invalid server status.
 *
 * @company consistec Engineering and Consulting GmbH
 * @date 10.10.12 11:22
 * @author marcel
 * @since 0.0.1-SNAPSHOT
 */
public class ServerStatusException extends SyncException {

    private ServerStatus status;

    /**
     *
     */
    public ServerStatusException() {
    }

    /**
     *
     * @param status Server status
     */
    public ServerStatusException(ServerStatus status) {
        this.status = status;
    }

    /**
     *
     * @param status Server status
     * @param msg Error message
     */
    public ServerStatusException(ServerStatus status, String msg) {
        super(msg);
        this.status = status;
    }

    /**
     *
     * @param message Error message
     */
    public ServerStatusException(String message) {
        super(message);
    }

    /**
     *
     * @param message Error message
     * @param cause The cause
     */
    public ServerStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     *
     * @param status Server status
     * @param message Error message
     * @param cause The cause
     */
    public ServerStatusException(ServerStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    /**
     *
     * @param cause Error message
     */
    public ServerStatusException(Throwable cause) {
        super(cause);
    }

    /**
     *
     * @param status Server status
     * @param cause The cause
     */
    public ServerStatusException(ServerStatus status, Throwable cause) {
        super(cause);
        this.status = status;
    }

    /**
     *
     * @return The status of the server.
     */
    public ServerStatus getStatus() {
        return status;
    }

}
