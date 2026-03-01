package io.github.ppp16bit.message_broker.binding;

import io.github.ppp16bit.message_broker.queue.BrokerQueue;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BindingRegistry {
    private final Map<String, List<BrokerQueue>> bindings = new ConcurrentHashMap<>();
    private final BrokerQueue brokerQueue;

    public BindingRegistry(BrokerQueue brokerQueue) {
        this.brokerQueue = brokerQueue;
    }

    @PostConstruct
    public void init() {
        bind("test", brokerQueue);
    }

    public void bind(String routingKey, BrokerQueue queue) {
        bindings.computeIfAbsent(routingKey, k -> new ArrayList<>()).add(queue);
    }

    public List<BrokerQueue> getQueues(String routingKey) {
        return bindings.getOrDefault(routingKey, Collections.emptyList());
    }
}