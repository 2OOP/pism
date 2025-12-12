package org.toop.framework.networking.server;

import java.net.InetSocketAddress;

public interface ServerUser {
    long id();
    String name();
    void setName(String name);
}
