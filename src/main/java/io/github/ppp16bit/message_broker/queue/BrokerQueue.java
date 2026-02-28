package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BrokerQueue {

    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();

    public void enqueue(Message message) {
        queue.add(message);
    }

    public Message consume() throws InterruptedException {
        return queue.take();
    }
}