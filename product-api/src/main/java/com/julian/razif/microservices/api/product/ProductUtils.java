package com.julian.razif.microservices.api.product;

import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProductUtils {

  private ProductUtils() {
  }

  public static UUID parseUUID(String s) {
    try {
      return s == null ? null : java.util.UUID.fromString(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  public static BigDecimal parseBigDecimal(String s) {
    try {
      return s == null ? null : new BigDecimal(s.trim());
    } catch (Exception e) {
      return null;
    }
  }

  public static ResponseEntity<Map<String, Object>> notFound() {
    return ResponseEntity.status(404).body(Map.of("errors", List.of("not found")));
  }

  public static ResponseEntity<Map<String, Object>> badUUID(String s) {
    return ResponseEntity.status(400).body(Map.of("errors", List.of("invalid input syntax for type uuid: \"" + s + "\"")));
  }

}
