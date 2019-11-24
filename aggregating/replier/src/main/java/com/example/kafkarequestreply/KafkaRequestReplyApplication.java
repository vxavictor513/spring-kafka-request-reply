package com.example.kafkarequestreply;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@Log4j2
public class KafkaRequestReplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaRequestReplyApplication.class, args);
	}

	@KafkaListener(id="server-a", topics = "kRequests")
	@SendTo("kReplies")
	public String listenerA(String in) {
		log.info("A - Server received: {}", in);
		return in.toUpperCase();
	}

	@KafkaListener(id="server-b", topics = "kRequests")
	@SendTo("kReplies")
	public String listenerB(String in) throws InterruptedException {
		// Thread.sleep(10100);
		log.info("B - Server received: {}", in);
		return "PROCESSED - " + in;
	}

	@Bean
	public NewTopic kRequests() {
		return TopicBuilder.name("kRequests")
				.partitions(2)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic kReplies() {
		return TopicBuilder.name("kReplies")
				.partitions(2)
				.replicas(1)
				.build();
	}

	/*@Bean // not required if Jackson is on the classpath
	public MessagingMessageConverter simpleMapperConverter() {
		MessagingMessageConverter messagingMessageConverter = new MessagingMessageConverter();
		messagingMessageConverter.setHeaderMapper(new SimpleKafkaHeaderMapper());
		return messagingMessageConverter;
	}*/
}
