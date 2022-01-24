package domain.concurrency.loadbalancer;

import lombok.Value;

@Value
public class Server {

    private String ip;
    private int port;
}
