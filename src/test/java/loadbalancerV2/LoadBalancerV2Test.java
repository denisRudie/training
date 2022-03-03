package loadbalancerV2;

import loadbalancerV2.exception.NotServersExistException;
import loadbalancerV2.exception.ServerAlreadyExistsException;
import loadbalancerV2.exception.ServerNotValidException;
import loadbalancerV2.exception.ServersCapacityReachedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadBalancerV2Test {

    final String IP = "127.0.0.1";
    final int PORT = 8080;
    LoadBalancerV2 loadBalancerV2;

    @BeforeEach
    void beforeEach() {
        loadBalancerV2 = new LoadBalancerV2Impl(10);
    }

    @Test
    void whenAddServer_thenThrowsException_ifCapacityReached() {
        for (int i = 0; i < 10; i++) {
            loadBalancerV2.add(new ServerInfo(IP, PORT + i));
        }

        Assertions.assertThrows(ServersCapacityReachedException.class, () -> loadBalancerV2.add(new ServerInfo(IP, PORT)));
    }

    @Test
    void whenAddServer_thenThrowsException_ifServerAlreadyExists() {
        loadBalancerV2.add(new ServerInfo(IP, PORT));

        Assertions.assertThrows(ServerAlreadyExistsException.class, () -> loadBalancerV2.add(new ServerInfo(IP, PORT)));
    }

    @Test
    void whenGetServer_thenReturnsFirstServer_ifOnlyOneServerExists() {
        ServerInfo server = new ServerInfo(IP, PORT);
        loadBalancerV2.add(server);

        assertEquals(server, loadBalancerV2.get(ServerSelectorStrategy.RANDOM));
    }

    @Test
    void whenGetServer_thenReturnRandomServer_randomCase() {
        loadBalancerV2 = new LoadBalancerV2Impl(1000);

        for (int i = 0; i < 1000; i++) {
            loadBalancerV2.add(new ServerInfo(IP, PORT + i));
        }

        Set<ServerInfo> randomServers = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            randomServers.add(loadBalancerV2.get(ServerSelectorStrategy.RANDOM));
        }

        Assertions.assertTrue(randomServers.size() > 1);
    }

    @Test
    void whenGetServer_thenAlwaysReturnMockedRandom_concurrentRandomCase() {
        loadBalancerV2 = new LoadBalancerV2Impl(10);

        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        List<ServerInfo> serversForAdding = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            serversForAdding.add(new ServerInfo(IP, PORT + i));
        }

        serversForAdding.forEach(serverInfo -> executorService.execute(() -> loadBalancerV2.add(serverInfo)));

        Map<ServerInfo, Long> foundedServers = IntStream.range(0, 100)
                .mapToObj(i ->  loadBalancerV2.get(ServerSelectorStrategy.RANDOM))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertTrue(foundedServers.size() > 1);
    }

    @Test
    void whenGetServer_thenThrowsException_ifNotServersExist_randomCase() {
        Assertions.assertThrows(NotServersExistException.class, () -> loadBalancerV2.get(ServerSelectorStrategy.RANDOM));
    }

    @Test
    void whenGetServer_thenReturnServersInAddedOrder_roundRobinCase() {
        List<ServerInfo> addedServers = IntStream.range(0, 10)
                .mapToObj(i -> new ServerInfo(IP, PORT + i))
                .collect(Collectors.toList());

        addedServers.forEach(loadBalancerV2::add);

        List<ServerInfo> receivedServers = IntStream.range(0, 10)
                .mapToObj(i -> loadBalancerV2.get(ServerSelectorStrategy.ROUND_ROBIN))
                .collect(Collectors.toList());

        assertEquals(addedServers, receivedServers);
    }

    @Test
    void whenGetServer_thenThrowsException_ifNotServersExist_roundRobinCase() {
        Assertions.assertThrows(NotServersExistException.class, () -> loadBalancerV2.get(ServerSelectorStrategy.ROUND_ROBIN));
    }

    @Test
    void whenAddServer_thenThrowsException_ifServerNotValid() {
        Assertions.assertThrows(ServerNotValidException.class, () -> loadBalancerV2.add(null));
    }
}
