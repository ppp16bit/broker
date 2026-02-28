package io.github.ppp16bit.message_broker.service;

import org.springframework.stereotype.Service;

import io.github.ppp16bit.message_broker.exchange.DirectExchange;
import io.github.ppp16bit.message_broker.model.Message;

@Service
public class ProducerService {
    private final DirectExchange exchange;

    public ProducerService(DirectExchange exchange) {
        this.exchange = exchange;
    }

    public void publish(Message message) {
        exchange.route(message);
    }
}