package org.toop.server.backend;

import org.toop.server.ServerMessage;

public interface IBackend {
    ServerMessage login(String username);
//    boolean isValidBackend(String backend);
}

