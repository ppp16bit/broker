package io.github.ppp16bit.message_broker.model;

public record Message(
    String id,
    String routingKey,
    String payload,
    long timestamp
) {}
