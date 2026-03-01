package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BrokerQueue {
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<String, InFlightMessage> inFlight = new ConcurrentHashMap<>();

    private int maxRetries = 3;
    private long messageTimeoutMillis = 10_000;
    private BrokerQueue deadLetterQueue;

    public void enqueue(Message message) {
        System.out.println("[ >>> ENQUEUE: " + message.id() + " <<< ]");
        queue.add(message);
    }

    public Message deliver() throws InterruptedException {
        Message message = queue.take();

        inFlight.put(
                message.id(),
                new InFlightMessage(
                        message,
                        System.currentTimeMillis(),
                        0
                )
        );
        return message;
    }

    public void ack(String messageId) {
        inFlight.remove(messageId);
    }

    public void requeue(String messageId) {
        InFlightMessage in = inFlight.remove(messageId);
        if (in == null) return;

        int nextRetry = in.retryCount() + 1;

        if (nextRetry > maxRetries) {
            sendToDlq(in.message());
            return;
        }
        queue.add(in.message());
        inFlight.put(
                in.message().id(),
                new InFlightMessage(
                        in.message(),
                        System.currentTimeMillis(),
                        nextRetry
                )
        );
    }

    private void sendToDlq(Message message) {
        if (deadLetterQueue != null) {
            deadLetterQueue.enqueue(message);
        }
    }

    public void setDeadLetterQueue(BrokerQueue deadLetterQueue) {
        this.deadLetterQueue = deadLetterQueue;
    }
    
    @Scheduled(fixedDelay = 1000)
    public void checkTimeouts() {
        long now = System.currentTimeMillis();
        
        for (Map.Entry<String, InFlightMessage> entry : inFlight.entrySet()) {
            InFlightMessage in = entry.getValue();
            
            if (now - in.deliveryTimestamp() > messageTimeoutMillis) {
                System.out.println("[ >>> TIMEOUT: " + in.message().id() + " <<< ]");
                requeue(in.message().id());
            }
        }
    }
}