package com.example.kafkarequestreply;

import java.time.LocalTime;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.AggregatingReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;

@SpringBootApplication
@Log4j2
public class KafkaRequestReplyApplication {

	@Value("${spring.application.name}")
	private String applicationName;
	@Value("${kafka.request.message:foo}")
	private String requestMessage;
	@Value("${kafka.reply.group:repliesGroup-0}")
	private String replyGroup;

	public static void main(String[] args) {
		SpringApplication.run(KafkaRequestReplyApplication.class, args);
	}

	@Bean
	public ApplicationRunner runner(AggregatingReplyingKafkaTemplate<Integer, String, String> template) {
		return args -> {
			while (true) {
				try {
					ProducerRecord<Integer, String> record = new ProducerRecord<>(
							"kRequests",
							"App: " + applicationName + " | Message: " + requestMessage + "-" + LocalTime.now()
					);
					RequestReplyFuture<Integer, String, Collection<ConsumerRecord<Integer, String>>> replyFuture =
							template.sendAndReceive(record);
					SendResult<Integer, String> sendResult = replyFuture.getSendFuture().get(10, TimeUnit.SECONDS);
					log.info("Sent ok: {}", sendResult.getRecordMetadata());
					ConsumerRecord<Integer, Collection<ConsumerRecord<Integer, String>>> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
					log.info("Return value: {}", consumerRecord.value());
					Thread.sleep(2000);
				} catch (Exception ex) {
					log.error("Something went wrong! Kafka reply timed out!", ex);
				}
			}
		};
	}

	@Bean
	public AggregatingReplyingKafkaTemplate<Integer, String, String> replyingTemplate(
			ProducerFactory<Integer, String> pf,
			ConcurrentMessageListenerContainer<Integer, Collection<ConsumerRecord<Integer, String>>> repliesContainer) {

		AggregatingReplyingKafkaTemplate<Integer, String, String> template =
				new AggregatingReplyingKafkaTemplate<>(pf, repliesContainer, coll -> coll.size() == 2);
		template.setSharedReplyTopic(true);
		template.setReturnPartialOnTimeout(true);
		return template;
	}

	@Bean
	public ConcurrentMessageListenerContainer<Integer, Collection<ConsumerRecord<Integer, String>>> repliesContainer(
			ConcurrentKafkaListenerContainerFactory<Integer, Collection<ConsumerRecord<Integer, String>>> containerFactory) {

		ConcurrentMessageListenerContainer<Integer, Collection<ConsumerRecord<Integer, String>>> repliesContainer =
				containerFactory.createContainer("kReplies");
		repliesContainer.getContainerProperties().setGroupId(replyGroup); // Overrides any `group.id` property provided by the consumer factory configuration
		repliesContainer.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

		repliesContainer.setAutoStartup(false);
		return repliesContainer;
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
}
