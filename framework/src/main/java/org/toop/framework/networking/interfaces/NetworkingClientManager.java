package org.toop.framework.networking.interfaces;

import org.toop.framework.networking.exceptions.ClientNotFoundException;
import org.toop.framework.networking.exceptions.CouldNotConnectException;
import org.toop.framework.networking.types.NetworkingConnector;

public interface NetworkingClientManager {
    void startClient(
            long id,
            NetworkingClient nClient,
            NetworkingConnector nConnector,
            Runnable onSuccess,
            Runnable onFailure
    ) throws CouldNotConnectException;
    void sendCommand(long id, String command) throws ClientNotFoundException;
    void closeClient(long id) throws ClientNotFoundException;
}
