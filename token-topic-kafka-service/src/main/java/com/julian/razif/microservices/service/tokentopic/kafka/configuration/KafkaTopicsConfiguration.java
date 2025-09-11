package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicsConfiguration {

  @Bean
  public NewTopic tokenCreatedTopic(@Value("${app.kafka.topics.tokenCreated}") String topic) {
    return new NewTopic(topic, 1, (short) 3);
  }

  @Bean
  public NewTopic tokenProcessedTopic(@Value("${app.kafka.topics.tokenProcessed}") String topic) {
    return new NewTopic(topic, 1, (short) 3);
  }

}
