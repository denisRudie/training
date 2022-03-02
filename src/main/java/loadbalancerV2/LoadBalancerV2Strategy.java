package loadbalancerV2;

import java.util.List;

public interface LoadBalancerV2Strategy {
    ServerInfo get(List<ServerInfo> servers);
}
