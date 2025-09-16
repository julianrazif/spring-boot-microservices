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

    Map<String, Object> product = new java.util.LinkedHashMap<>();
    product.put("id", java.util.UUID.fromString(productId));
    product.put("name", "Sample Product");
    product.put("image_url", "https://example.com/image.jpg");
    product.put("price", "1200000");
    product.put("stock", 9);

    Map<String, Object> cartItem = new java.util.LinkedHashMap<>();
    cartItem.put("product", product);
    cartItem.put("quantity", 1);

    java.util.List<Map<String, Object>> carts = java.util.List.of(cartItem);

    Map<String, Object> data = new java.util.LinkedHashMap<>();
    data.put("customerName", "");
    data.put("customerEmail", "");
    data.put("carts", carts);
    data.put("itemCount", 1);
    data.put("totalPrice", 1200000);

    return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Collections.singletonMap("data", data));
  }

  @PutMapping("/customer/carts/{productId}")
  public ResponseEntity<?> update(@PathVariable("productId") String productId) {
    if (parseUUID(productId) == null) return badUUID(productId);

    Map<String, Object> product = new java.util.LinkedHashMap<>();
    product.put("id", java.util.UUID.fromString(productId));
    product.put("name", "Sample Product");
    product.put("image_url", "https://example.com/image.jpg");
    product.put("price", "1200000");
    product.put("stock", 9);

    Map<String, Object> cartItem = new java.util.LinkedHashMap<>();
    cartItem.put("product", product);
    cartItem.put("quantity", 1);

    java.util.List<Map<String, Object>> carts = java.util.List.of(cartItem);

    Map<String, Object> data = new java.util.LinkedHashMap<>();
    data.put("customerName", "");
    data.put("customerEmail", "");
    data.put("carts", carts);
    data.put("itemCount", 1);
    data.put("totalPrice", 1200000);

    return ResponseEntity.ok(java.util.Collections.singletonMap("data", data));
  }

  @DeleteMapping("/customer/carts/{productId}")
  public ResponseEntity<?> delete(@PathVariable("productId") String productId) {
    if (parseUUID(productId) == null) return badUUID(productId);
    return ResponseEntity.ok(java.util.Collections.singletonMap("message", "products has been removed"));
  }

}
