package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;
import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BrokerQueue {
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<String, InFlightMessage> inFlight = new ConcurrentHashMap<>();
    private final DelayQueue<DelayedMessage> retryQueue = new DelayQueue<>();

    private int maxRetries = 3;
    private long messageTimeoutMillis = 10_000;
    private long baseBackoffMillis = 1000;
    private BrokerQueue deadLetterQueue;

    public void enqueue(Message message) {
        System.out.print("[ >>> ENQUEUE: " + message.id() + " <<< ]\n");
        queue.add(message);
    }

    public Message deliver() throws InterruptedException {
        Message message = queue.take();
        inFlight.computeIfAbsent(
            message.id(),
            id -> new InFlightMessage(
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

        long delay = baseBackoffMillis * (1L << (nextRetry - 1));
        retryQueue.offer(
            new DelayedMessage(in.message(), delay, nextRetry)
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
                System.out.print("[ >>> TIMEOUT: " + in.message().id() + " <<< ]\n");
                requeue(in.message().id());
            }
        }
    }

    @PostConstruct
    public void startRetryWorker() {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    DelayedMessage delayed = retryQueue.take();
                    queue.add(delayed.getMessage());
                } catch (InterruptedException ignored) {}
            }
        });
    }

    public int readySize() { return queue.size(); }
    public int inFlightSize() { return inFlight.size(); }
    public int retrySize() { return retryQueue.size(); }

    @Scheduled(fixedDelay = 5000)
    public void printMetrics() {
        System.out.println("""
            ---------------------------
            | ===  BROKER METRICS === |
            |-------------------------|
            | READY: [%d]             |
            | INFLIGHT: [%d]          |
            | RETRY: [%d]             |
            |-------------------------|
            """.formatted(
                readySize(),
                inFlightSize(),
                retrySize()
            ));
    }

    // test
    @PostConstruct
    public void loadTestMessages() {
        for (int i = 0; i < 20; i++) {
            enqueue(new Message(
                String.valueOf(i),
                "test.key",
                "payload-" + i,
                System.currentTimeMillis()
            ));
        }
    }
}