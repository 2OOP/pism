package org.toop.framework.networking.server.stores;

import java.util.Collection;

public interface Store<IDENTIFIER, STORED> {
    void add(STORED adding);
    void remove(IDENTIFIER remover);
    STORED get(IDENTIFIER getter);
    Collection<STORED> all();
}
