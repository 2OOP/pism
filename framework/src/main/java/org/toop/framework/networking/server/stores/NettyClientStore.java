package org.toop.framework.networking.server.stores;

import org.toop.framework.networking.server.client.NettyClient;

import java.util.Collection;
import java.util.Map;

public class NettyClientStore implements ClientStore<Long, NettyClient> {
    final private Map<Long, NettyClient> users;

    public NettyClientStore(Map<Long, NettyClient> usersMap) {
        this.users = usersMap;
    }

    @Override
    public void add(NettyClient adding) {
        users.putIfAbsent(adding.id(), adding);
    }

    @Override
    public void remove(Long remover) {
        users.remove(remover);
    }

    @Override
    public NettyClient get(Long getter) {
        return users.get(getter);
    }

    @Override
    public Collection<NettyClient> all() {
        return users.values();
    }
}
