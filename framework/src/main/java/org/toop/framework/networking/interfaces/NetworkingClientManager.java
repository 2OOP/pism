package org.toop.framework.networking.interfaces;

import java.util.OptionalLong;

public interface NetworkingClientManager {
    OptionalLong startClient(long id, NetworkingClient networkingClientClass, String host, int port);
    boolean sendCommand(long id, String command);
    boolean reconnect(long id);
    boolean changeAddress(long id, String host, int port);
    boolean closeClient(long id);
}
