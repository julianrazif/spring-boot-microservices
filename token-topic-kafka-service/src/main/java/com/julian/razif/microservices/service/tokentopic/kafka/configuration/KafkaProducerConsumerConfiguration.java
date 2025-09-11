package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julian.razif.microservices.service.tokentopic.kafka.dto.TokenCreatedEvent;
import com.julian.razif.microservices.service.tokentopic.kafka.dto.TokenProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConsumerConfiguration {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrap;

  @Value("${app.kafka.groupId}")
  private String groupId;

  private final ObjectMapper mapper;
  private final OSHostName osHostName;

  @Bean(name = "tokenEventConsumerFactory")
  public ConsumerFactory<String, TokenCreatedEvent> tokenEventConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
    return new DefaultKafkaConsumerFactory<>(
      props,
      new StringDeserializer(),
      new JacksonDeserializer<>(mapper, TokenCreatedEvent.class)
    );
  }

  @Bean(name = "tokenEventListenerContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<String, TokenCreatedEvent> tokenEventListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, TokenCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(tokenEventConsumerFactory());
    return factory;
  }

  @Bean(name = "tokenProcessedEventProducerFactory")
  public ProducerFactory<String, TokenProcessedEvent> tokenProcessedEventProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, osHostName.getClientId());
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);
    return new DefaultKafkaProducerFactory<>(
      props,
      new StringSerializer(),
      new JacksonSerializer<>(mapper)
    );
  }

  @Bean(name = "tokenProcessedEventKafkaTemplate")
  public KafkaTemplate<String, TokenProcessedEvent> tokenProcessedEventKafkaTemplate() {
    return new KafkaTemplate<>(tokenProcessedEventProducerFactory());
  }

}
