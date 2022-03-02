package loadbalancerV2;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class LoadBalancerRandomSelector implements LoadBalancerV2Strategy {

    private static LoadBalancerRandomSelector instance;

    private LoadBalancerRandomSelector() {
    }

    public static LoadBalancerRandomSelector getInstance() {
        return Objects.isNull(instance) ? (instance = new LoadBalancerRandomSelector()) : instance;
    }

    @Override
    public ServerInfo get(List<ServerInfo> servers) {
        return servers.get(ThreadLocalRandom.current().nextInt(servers.size()));
    }
}
