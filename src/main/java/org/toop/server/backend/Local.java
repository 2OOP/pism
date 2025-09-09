package org.toop.server.backend;

import org.toop.server.Server;

public class Local implements Backend {
    @Override
    public Server.Message login(String username) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Local; // TODO
    }

    @Override
    public String toString() {
        return "Local";
    }

}
