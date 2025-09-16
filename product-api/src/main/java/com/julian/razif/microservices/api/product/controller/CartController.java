package com.julian.razif.microservices.api.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.julian.razif.microservices.api.product.ProductUtils.badUUID;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
public class CartController {

  @GetMapping("/customer/carts")
  public ResponseEntity<?> list() {
    Map<String, Object> data = new java.util.LinkedHashMap<>();
    data.put("customerName", "");
    data.put("customerEmail", "");
    data.put("carts", java.util.List.of());
    data.put("itemCount", 0);
    data.put("totalPrice", 0);
    return ResponseEntity.ok(java.util.Collections.singletonMap("data", data));
  }

  @PostMapping("/customer/carts/{productId}")
  public ResponseEntity<?> create(@PathVariable("productId") String productId) {
    if (parseUUID(productId) == null) return badUUID(productId);
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", "Not implemented in this demo"));
  }

  @PutMapping("/customer/carts/{productId}")
  public ResponseEntity<?> update(@PathVariable("productId") String productId) {
    if (parseUUID(productId) == null) return badUUID(productId);
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", "Not implemented in this demo"));
  }

  @DeleteMapping("/customer/carts/{productId}")
  public ResponseEntity<?> delete(@PathVariable("productId") String productId) {
    if (parseUUID(productId) == null) return badUUID(productId);
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(Map.of("message", "Not implemented in this demo"));
  }

}
