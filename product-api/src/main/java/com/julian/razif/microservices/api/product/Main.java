package com.julian.razif.microservices.api.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
  scanBasePackages = {
    "com.julian.razif.microservices.api.product",
    "com.julian.razif.microservices.service.persistence.product"
  }
)
public class Main {

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

}
