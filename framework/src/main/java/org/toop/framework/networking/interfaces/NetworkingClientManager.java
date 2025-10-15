package org.toop.framework.networking.interfaces;

import org.toop.framework.networking.exceptions.ClientNotFoundException;
import org.toop.framework.networking.exceptions.CouldNotConnectException;
import org.toop.framework.networking.types.NetworkingReconnect;

import java.util.Optional;

public interface NetworkingClientManager {
    void startClient(
            long id,
            NetworkingClient networkingClientClass,
            String host,
            int port,
            NetworkingReconnect networkingReconnect) throws CouldNotConnectException;
    void sendCommand(long id, String command) throws ClientNotFoundException;
    void reconnect(long id, NetworkingReconnect networkingReconnect) throws ClientNotFoundException;
    void changeAddress(long id, String host, int port, NetworkingReconnect networkingReconnect) throws ClientNotFoundException;
    void closeClient(long id) throws ClientNotFoundException;
}
