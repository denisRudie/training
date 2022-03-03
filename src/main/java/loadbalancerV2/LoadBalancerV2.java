package loadbalancerV2;


public interface LoadBalancerV2 {

    void add(ServerInfo server);

    ServerInfo get(ServerSelectorStrategy serverSelectorStrategy);
}
