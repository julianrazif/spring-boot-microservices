package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@WebFluxTest(controllers = CartController.class)
@Import(SecurityConfig.class)
class CartControllerSecurityAndFlowTest {

  @Autowired
  private WebTestClient webTestClient;

  private String customerUser;
  private String customerPass;

  @BeforeEach
  void setUp() {
    customerUser = "customer";
    customerPass = "customer123";
  }

  @Test
  @DisplayName("GET /customer/carts requires auth and returns 200 when authenticated")
  void getCarts_auth() {
    // Unauthenticated -> 401
    webTestClient.get().uri("/customer/carts")
      .exchange()
      .expectStatus().isUnauthorized()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("authentication failed");

    // Authenticated as CUSTOMER -> 200
    webTestClient.get().uri("/customer/carts")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.customerName").exists()
      .jsonPath("$.data.carts").isArray()
      .jsonPath("$.data.itemCount").isEqualTo(0)
      .jsonPath("$.data.totalPrice").isEqualTo(0);
  }

  @Test
  @DisplayName("POST /customer/carts/{productId} requires auth, validates UUID, and returns 201")
  void postCart_flow() {
    // Unauthenticated -> 401
    webTestClient.post().uri("/customer/carts/" + UUID.randomUUID())
      .exchange()
      .expectStatus().isUnauthorized();

    // Invalid UUID -> 400
    webTestClient.post().uri("/customer/carts/abc")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("invalid input syntax for type uuid: \"abc\"");

    // Valid -> 201 and structure
    UUID productId = UUID.randomUUID();
    webTestClient.post().uri("/customer/carts/" + productId)
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.data.carts[0].product.id").isEqualTo(productId.toString())
      .jsonPath("$.data.itemCount").isEqualTo(1)
      .jsonPath("$.data.totalPrice").isEqualTo(1200000);
  }

  @Test
  @DisplayName("PUT /customer/carts/{productId} requires auth, validates UUID, and returns 200")
  void putCart_flow() {
    // Unauthenticated -> 401
    webTestClient.put().uri("/customer/carts/" + UUID.randomUUID())
      .exchange()
      .expectStatus().isUnauthorized();

    // Invalid UUID -> 400
    webTestClient.put().uri("/customer/carts/abc")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("invalid input syntax for type uuid: \"abc\"");

    // Valid -> 200
    UUID productId = UUID.randomUUID();
    webTestClient.put().uri("/customer/carts/" + productId)
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.carts[0].product.id").isEqualTo(productId.toString())
      .jsonPath("$.data.itemCount").isEqualTo(1)
      .jsonPath("$.data.totalPrice").isEqualTo(1200000);
  }

  @Test
  @DisplayName("DELETE /customer/carts/{productId} requires auth, validates UUID, and returns 200")
  void deleteCart_flow() {
    // Unauthenticated -> 401
    webTestClient.delete().uri("/customer/carts/" + UUID.randomUUID())
      .exchange()
      .expectStatus().isUnauthorized();

    // Invalid UUID -> 400
    webTestClient.delete().uri("/customer/carts/abc")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("invalid input syntax for type uuid: \"abc\"");

    // Valid -> 200
    UUID productId = UUID.randomUUID();
    webTestClient.delete().uri("/customer/carts/" + productId)
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.message").isEqualTo("products has been removed");
  }
}
