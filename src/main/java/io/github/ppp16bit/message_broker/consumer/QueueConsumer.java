package io.github.ppp16bit.message_broker.consumer;

import io.github.ppp16bit.message_broker.model.Message;
import io.github.ppp16bit.message_broker.queue.BrokerQueue;
import io.github.ppp16bit.message_broker.clj.ClojureBridge;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    private final BrokerQueue brokerQueue;
    private final ClojureBridge clojureBridge;

    public QueueConsumer(BrokerQueue brokerQueue, ClojureBridge clojureBridge) {
        this.brokerQueue = brokerQueue;
        this.clojureBridge = clojureBridge;
    }

    @PostConstruct
    public void start() {
        System.out.print("[ >>> QUEUE CONSUMER STARTED <<< ]\n");

        Thread.ofVirtual().start(() -> {
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
        });
    }

    private void handle(Message msg) {
        try {
            clojureBridge.process(msg);
            brokerQueue.ack(msg.id());
            System.out.println("[ >>> ACK: " + msg.id() + " <<< ]\n");

        } catch (Exception e) {
            System.out.println("[ >>> FAIL -> REQUEUE: " + msg.id() + " <<< ]\n");
            brokerQueue.requeue(msg.id());
        }
    }
}