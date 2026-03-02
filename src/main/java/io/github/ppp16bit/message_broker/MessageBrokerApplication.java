package io.github.ppp16bit.message_broker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MessageBrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageBrokerApplication.class, args);
	}

}
