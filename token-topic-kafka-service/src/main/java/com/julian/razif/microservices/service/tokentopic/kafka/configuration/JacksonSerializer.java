package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;

@RequiredArgsConstructor
public class JacksonSerializer<T> implements Serializer<T> {

  private final ObjectMapper mapper;

  @Override
  public byte[] serialize(String topic, T data) {
    try {
      return data == null ? null : mapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
