package org.toop.server.backend.local;

import org.toop.server.ServerMessage;
import org.toop.server.backend.IBackend;

public class Local implements IBackend {
    @Override
    public ServerMessage login(String username) {
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
