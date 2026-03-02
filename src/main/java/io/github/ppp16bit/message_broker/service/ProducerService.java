package io.github.ppp16bit.message_broker.service;

import org.springframework.stereotype.Service;

import io.github.ppp16bit.message_broker.model.Message;
import io.github.ppp16bit.message_broker.queue.BrokerQueue;

@Service
public class ProducerService {
    private final BrokerQueue brokerQueue;

    public ProducerService(BrokerQueue brokerQueue) {
        this.brokerQueue = brokerQueue;
    }

    public void publish(Message message) {
        brokerQueue.enqueue(message);
    }
}