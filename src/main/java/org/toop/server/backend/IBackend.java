package org.toop.server.backend;

import org.toop.server.Server;

public interface IBackend {
    Server.Message login(String username);
//    boolean isValidBackend(String backend);
}

