package org.toop.framework.networking.exceptions;

/**
 * Thrown when an operation is attempted on a networking client
 * that does not exist or has already been closed.
 */
public class ClientNotFoundException extends RuntimeException {

    private final long clientId;

    public ClientNotFoundException(long clientId) {
        super("Networking client with ID " + clientId + " was not found.");
        this.clientId = clientId;
    }

    public ClientNotFoundException(long clientId, Throwable cause) {
        super("Networking client with ID " + clientId + " was not found.", cause);
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }

}