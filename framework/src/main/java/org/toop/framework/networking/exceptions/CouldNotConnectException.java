package org.toop.framework.networking.exceptions;

public class CouldNotConnectException extends RuntimeException {

  private final long clientId;

  public CouldNotConnectException(long clientId) {
    super("Networking client with ID " + clientId + " could not connect.");
    this.clientId = clientId;
  }

  public CouldNotConnectException(long clientId, Throwable cause) {
    super("Networking client with ID " + clientId + " could not connect.", cause);
    this.clientId = clientId;
  }

  public long getClientId() {
    return clientId;
  }

}