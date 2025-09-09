package org.toop.server.backend;

import org.toop.server.Server;

public interface Backend {
    Server.Message login(String username);
}

