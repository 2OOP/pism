package org.toop.server.backend.remote;

import org.toop.server.Server;
import org.toop.server.backend.IBackend;

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
