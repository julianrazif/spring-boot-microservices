package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import com.julian.razif.microservices.service.tokentopic.kafka.dto.TokenCreatedEvent;
import com.julian.razif.microservices.service.tokentopic.kafka.dto.TokenProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenListener {

  private final KafkaTemplate<String, TokenProcessedEvent> tokenProcessedEventKafkaTemplate;

  @Value("${app.kafka.topics.tokenProcessed}")
  private String tokenProcessedTopic;

  @KafkaListener(
    topics = "${app.kafka.topics.tokenCreated}",
    containerFactory = "tokenEventListenerContainerFactory"
  )
  public void onTokenCreated(@Payload TokenCreatedEvent event) {
    log.info("Received TokenCreatedEvent: {}", event);

    TokenProcessedEvent result = new TokenProcessedEvent()
      .token("token")
      .deviceId("deviceId");

    tokenProcessedEventKafkaTemplate.send(tokenProcessedTopic, event.deviceId(), result).completable()
      .whenComplete((md, ex) -> {
        if (ex != null) {
          log.error("Failed to publish TokenProcessedEvent", ex);
        } else {
          log.info("Published TokenProcessedEvent: {}", result);
        }
      });
  }

}
