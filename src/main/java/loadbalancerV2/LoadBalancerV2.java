package loadbalancerV2;

import java.util.Random;

public interface LoadBalancerV2 {

    void add(ServerInfo server);

    ServerInfo get(ServerSelectorStrategy serverSelectorStrategy);

    ServerInfo getRandom(Random random);
}
