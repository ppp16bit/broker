package io.github.ppp16bit.message_broker.consumer;

import io.github.ppp16bit.message_broker.queue.BrokerQueue;

import org.springframework.stereotype.Component;

@Component
public class QueueConsumer implements Runnable {
    private final BrokerQueue queue;

    public QueueConsumer(BrokerQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                var msg = queue.consume();
                process(msg);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    private void process(Object msg) {
        System.out.println("processing: " + msg);
    }
}