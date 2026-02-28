package io.github.ppp16bit.message_broker.exchange;

import io.github.ppp16bit.message_broker.binding.BindingRegistry;
import io.github.ppp16bit.message_broker.model.Message;
import org.springframework.stereotype.Component;

@Component
public class DirectExchange implements Exchange {
    private final BindingRegistry bindingRegistry;

    public DirectExchange(BindingRegistry bindingRegistry) {
        this.bindingRegistry = bindingRegistry;
    }

    @Override
    public void route(Message message) {
        var queues = bindingRegistry.getQueues(message.routingKey());
        queues.forEach(q -> q.enqueue(message));
    }
}