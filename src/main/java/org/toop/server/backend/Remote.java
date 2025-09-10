package org.toop.server.backend;

import org.toop.server.Server;

public class Remote implements IBackend {
    @Override
    public Server.Message login(String username) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Remote; // TODO
    }

    @Override
    public String toString() {
        return "Remote";
    }

}
