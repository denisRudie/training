package loadbalancerV2;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

public class LoadBalancerRoundRobinSelector implements LoadBalancerV2Strategy {

    private static LoadBalancerRoundRobinSelector instance;

    private LoadBalancerRoundRobinSelector() {
    }

    public static LoadBalancerRoundRobinSelector getInstance() {
        return isNull(instance) ? (instance = new LoadBalancerRoundRobinSelector()) : instance;
    }

    private final AtomicInteger roundRobinCounter = new AtomicInteger(0);

    @Override
    public ServerInfo get(List<ServerInfo> servers) {
        return servers.get(roundRobinCounter.getAndUpdate(counter -> counter >= servers.size() - 1 ? counter = 0 : counter + 1));
    }
}
