package loadbalancerV2;

import loadbalancerV2.exception.NotServersExistException;
import loadbalancerV2.exception.ServerAlreadyExistsException;
import loadbalancerV2.exception.ServerNotValidException;
import loadbalancerV2.exception.ServersCapacityReachedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancerV2Impl implements LoadBalancerV2 {

    private final List<ServerInfo> servers;
    private final int capacity;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    public LoadBalancerV2Impl(int capacity) {
        this.servers = new ArrayList<>();
        this.capacity = capacity;
    }

    @Override
    public void add(ServerInfo server) {
        if (!validateServer(server)) {
            throw new ServerNotValidException();
        }

        readWriteLock.writeLock().lock();

        try {
            if (servers.size() >= capacity) {
                throw new ServersCapacityReachedException();
            }
            if (servers.contains(server)) {
                throw new ServerAlreadyExistsException();
            }
            servers.add(server);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public ServerInfo get(ServerSelectorStrategy serverSelectorStrategy) {
        readWriteLock.readLock().lock();

        try {
            if (servers.isEmpty()) {
                throw new NotServersExistException();
            }
            switch (serverSelectorStrategy) {
                case RANDOM:
                    return LoadBalancerRandomSelector.getInstance()
                            .get(servers);
                case ROUND_ROBIN:
                    return LoadBalancerRoundRobinSelector.getInstance()
                            .get(servers);
                default:
                    throw new UnsupportedOperationException();
            }
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private boolean validateServer(ServerInfo server) {
        if (Objects.isNull(server)) {
            return false;
        }

        if (Objects.isNull(server.getIp()) || server.getIp().isBlank() || server.getPort() == 0 || server.getPort() < 0) {
            return false;
        }

        return true;
    }
}
