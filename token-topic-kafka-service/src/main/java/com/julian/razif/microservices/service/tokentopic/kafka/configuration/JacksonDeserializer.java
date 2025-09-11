package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Deserializer;

@RequiredArgsConstructor
public class JacksonDeserializer<T> implements Deserializer<T> {

  private final ObjectMapper mapper;
  private final Class<T> clazz;

  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      return data == null ? null : mapper.readValue(data, clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
