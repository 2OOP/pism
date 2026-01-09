package org.toop.framework.networking.server.connectionHandler;

import org.toop.framework.networking.server.client.Client;

public interface ClientSession<G, P> {
    Client<G, P> client();
}
