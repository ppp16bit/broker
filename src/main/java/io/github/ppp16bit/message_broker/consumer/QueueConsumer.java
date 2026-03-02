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
        System.out.print("[ >>> Queue consumer started <<< ]\n");
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
            brokerQueue.ack(msg.id());
            System.out.print("[ >>> ACK: " + msg.id() + " <<< ]\n");
        } catch (Exception e) {
            System.out.print("[ >>> FAIL -> REQUEUE: " + msg.id() + " <<< ]\n");
            brokerQueue.enqueue(msg);
        }
    }

    private void process(Message msg) {
        System.out.print("[ >>> PROCESSING: " + msg + " <<< ]\n");
        System.out.print("[ >>> PROCESSED SUCCESFULLY: " + msg.id() + " <<< ]\n");
    }
}