package loadbalancer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static loadbalancer.LoadBalancer.CAPACITY;

public class LoadBalancerTest {

    private static final String IP = "192.168.0.1";
    private static int PORT = 5432;

    @Test
    void whenAddServer_shouldAddCorrectlyAdd() {
        LoadBalancer loadBalancer = new LoadBalancer();

        Server server = new Server(IP, PORT);
        loadBalancer.addServer(server);

        Assertions.assertEquals(server, loadBalancer.getServers().get(0));
    }

    @Test
    void whenGetServer_ShouldReturnServer_IfRoundRobinStrategy() {
        LoadBalancer loadBalancer = new LoadBalancer();

        Server server1 = new Server(IP, PORT);
        Server server2 = new Server(IP, ++PORT);
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        Assertions.assertEquals(server1, loadBalancer.getServerRoundRobin());
        Assertions.assertEquals(server2, loadBalancer.getServerRoundRobin());
    }

    @Test
    void whenGetServer_ShouldReturnServer_IfRandomStrategy() {
        LoadBalancer loadBalancer = new LoadBalancer();

        Server server1 = new Server(IP, PORT);
        Server server2 = new Server(IP, ++PORT);
        loadBalancer.addServer(server1);
        loadBalancer.addServer(server2);

        Assertions.assertNotNull(loadBalancer.getServerRandom());
    }

    @Test
    void whenGetServer_ShouldThrowException_IfNotServersExist() {
        LoadBalancer loadBalancer = new LoadBalancer();

        Assertions.assertThrows(RuntimeException.class, loadBalancer::getServerRoundRobin);
    }

    @Test
    void whenAddServer_ShouldThrowException_IfCapacityReached() {
        LoadBalancer loadBalancer = new LoadBalancer();

        for (int i = 0; i < CAPACITY; i++) {
            loadBalancer.addServer(new Server(IP, ++PORT));
        }

        Assertions.assertThrows(RuntimeException.class, () ->
                loadBalancer.addServer(new Server(IP, ++PORT)));
    }
}
