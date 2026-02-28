package io.github.ppp16bit.message_broker.exchange;

import io.github.ppp16bit.message_broker.model.Message;

public interface Exchange {
    void route(Message message);
}