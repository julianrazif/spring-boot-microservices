package com.julian.razif.microservices.service.tokentopic.kafka.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class OSHostName {

  @Value("${app.kafka.clientId}")
  private String clientId;

  public String getHostName() {
    String hostName = "-localhost";
    try {
      hostName = "-" + InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      // ignore
    }
    hostName += "-" + (new Random().nextInt(5 * 3 - 1 + 1) + 1) + "-" + (new Random().nextInt(15 * 3 - 1 + 1) + 1);
    return hostName;
  }

  public String getClientId() {
    return clientId + getHostName();
  }

}
