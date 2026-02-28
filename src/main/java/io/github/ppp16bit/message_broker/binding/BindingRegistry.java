package io.github.ppp16bit.message_broker.binding;

import io.github.ppp16bit.message_broker.queue.BrokerQueue;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BindingRegistry {
    private final Map<String, List<BrokerQueue>> bindings = new ConcurrentHashMap<>();

    public void bind(String routingKey, BrokerQueue queue) {
        bindings.computeIfAbsent(routingKey, k -> new ArrayList<>()).add(queue);
    }

    public List<BrokerQueue> getQueues(String routingKey) {
        if (routingKey == null) {
            return List.of();
        }
        return bindings.getOrDefault(routingKey, List.of());
    }
}