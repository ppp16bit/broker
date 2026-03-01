package io.github.ppp16bit.message_broker.queue;

import io.github.ppp16bit.message_broker.model.Message;

public record InFlightMessage(
        Message message,
        long deliveryTimestamp,
        int retryCount
) {}