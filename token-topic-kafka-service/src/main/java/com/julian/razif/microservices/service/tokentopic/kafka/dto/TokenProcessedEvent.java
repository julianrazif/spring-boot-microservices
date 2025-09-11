package com.julian.razif.microservices.service.tokentopic.kafka.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true, chain = true)
@Getter(onMethod_ = {@JsonProperty})
public class TokenProcessedEvent {

  private String deviceId;
  private String token;

}
