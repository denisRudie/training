package app.concurrency.loadbalancer;

import domain.concurrency.loadbalancer.Server;
import lombok.Data;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class LoadBalancer {

    public static final int CAPACITY = 10;
    private List<Server> servers;

    private int roundRobinCounter = 0;
    private ReentrantLock lock;

    public LoadBalancer() {
        servers = new CopyOnWriteArrayList<>();
        lock = new ReentrantLock(true);
    }

    public void addServer(Server server) {
        if (servers.size() < 10) {
            this.servers.add(server);
        } else {
            throw new RuntimeException("Server limit reached");
        }
    }

    public Server getServerRoundRobin() {
        if (servers.isEmpty()) {
            throw new RuntimeException("Not any servers exist");
        }

        lock.lock();
        try {
            return servers.get(roundRobinCounter % servers.size());
        } finally {
            lock.unlock();
        }
    }

    public Server getServerRandom() {
        if (servers.isEmpty()) {
            throw new RuntimeException("Not any servers exist");
        }

        Random random = new Random();
        return this.servers.get(random.nextInt(servers.size()));
    }

}
