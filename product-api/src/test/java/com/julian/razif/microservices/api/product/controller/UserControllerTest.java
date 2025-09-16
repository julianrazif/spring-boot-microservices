package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.UserService;
import com.julian.razif.microservices.service.persistence.product.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private UserService userService;

  private User sampleUser(String displayName, String email, String role) {
    User u = new User();
    u.setDisplayName(displayName);
    u.setEmail(email);
    u.setRole(role);
    return u;
  }

  @Test
  @DisplayName("POST /login returns 200 with access_token when body provided")
  void login_ok_withBody() {
    String body = "{\n  \"email\": \"admin@gmail.com\",\n  \"password\": \"secret\"\n}";

    webTestClient.post().uri("/login")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.access_token").isEqualTo("basic");
  }

  @Test
  @DisplayName("POST /login returns 200 with access_token when body is missing")
  void login_ok_withoutBody() {
    webTestClient.post().uri("/login")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.access_token").isEqualTo("basic");
  }

  @Test
  @DisplayName("POST /register returns 201 with message and user info when email unique")
  void register_created() {
    String email = "arnold@gmail.com";
    when(userService.emailExists(email)).thenReturn(false);
    when(userService.register(any())).thenReturn(sampleUser("Arnold", email, "customer"));

    String body = "{\n" +
      "  \"displayName\": \"Arnold\",\n" +
      "  \"email\": \"" + email + "\",\n" +
      "  \"password\": \"abcd\"\n" +
      "}";

    webTestClient.post().uri("/register")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.data.message").isEqualTo("register success")
      .jsonPath("$.data.displayName").isEqualTo("Arnold")
      .jsonPath("$.data.email").isEqualTo(email)
      .jsonPath("$.data.role").isEqualTo("customer");
  }

  @Test
  @DisplayName("POST /register returns 400 when email already taken")
  void register_emailTaken_badRequest() {
    String email = "dup@gmail.com";
    when(userService.emailExists(email)).thenReturn(true);

    String body = "{\n" +
      "  \"displayName\": \"User\",\n" +
      "  \"email\": \"" + email + "\",\n" +
      "  \"password\": \"abcd\"\n" +
      "}";

    webTestClient.post().uri("/register")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("email already has taken");
  }

  @Test
  @DisplayName("POST /register with invalid body returns 400 with validation errors")
  void register_invalidBody_validationErrors() {
    String body = "{}";

    webTestClient.post().uri("/register")
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors").value(arr -> {
        java.util.List<?> list = (java.util.List<?>) arr;
        java.util.List<String> strings = list.stream().map(Object::toString).toList();
        org.assertj.core.api.Assertions.assertThat(strings)
          .contains("name is required", "email is required", "password is required");
      });
  }

}
