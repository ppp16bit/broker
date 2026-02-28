package io.github.ppp16bit.message_broker.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.ppp16bit.message_broker.model.Message;
import io.github.ppp16bit.message_broker.service.ProducerService;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final ProducerService producerService;

    public MessageController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/publish")
    public Message publish(@RequestBody Message message) {
        producerService.publish(message);
        return message;
    }
}