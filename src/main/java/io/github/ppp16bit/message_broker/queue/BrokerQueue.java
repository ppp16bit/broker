package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class BrokerQueue {
    private final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
    private final Map<String, Message> inFLight = new ConcurrentHashMap<>();

    public void enqueue(Message message) {
         System.out.print("[ >>> ENQUEUE: " + message.id () + "<<< ]");
        queue.add(message);
    }

    public Message deliver() throws InterruptedException {
        Message message = queue.take();
        inFLight.put(message.id(), message);
        return message;
    }

    public void ack(String messageID) {
        inFLight.remove(messageID);
    }

    public void requeue(String messageID) {
        Message message = inFLight.remove(messageID);
        if (message != null)
            queue.add(message);
    }
}