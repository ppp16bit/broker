package io.github.ppp16bit.message_broker.consumer;

import io.github.ppp16bit.message_broker.model.Message;
import io.github.ppp16bit.message_broker.queue.BrokerQueue;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {
    private final BrokerQueue brokerQueue;

    public QueueConsumer(BrokerQueue brokerQueue) {
        this.brokerQueue = brokerQueue;
    }

    @PostConstruct
    public void start() {
        System.out.print("[ >>> queue consumer started <<< ]");
        new Thread(() -> {
            while (true) {
                try {
                    Message msg = brokerQueue.deliver();
                    handle(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "broker-consumer-thread").start();
    }

    private void handle(Message msg) {
        try {
            process(msg);
            System.out.print("[ >>> ACK: " + msg.id() + " <<< ]");
        } catch (Exception e) {
            System.out.print("[ >>> FAIL -> requeue: " + msg.id() + " <<< ]");
            brokerQueue.enqueue(msg);
        }
    }

    private void process(Message msg) {
        System.out.print("[ >>> processing: " + msg + " <<< ]");
        System.out.print("[ >>> processed successfully: " + msg.id() + " <<< ]");
    }
}