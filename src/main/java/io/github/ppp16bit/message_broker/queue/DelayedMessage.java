package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedMessage implements Delayed {
    private final Message message;
    private final long triggerTime;
    private final int retryCount;

    public DelayedMessage(Message message, long delayMillis, int retryCount) {
        this.message = message;
        this.triggerTime = System.currentTimeMillis() + delayMillis;
        this.retryCount = retryCount;
    }

    public Message getMessage() { return message; }
    public int getRetryCount() { return retryCount; }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = triggerTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.triggerTime,
                ((DelayedMessage) o).triggerTime);
    }
}