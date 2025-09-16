package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.CategoryDTO;
import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.CategoryService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CategoryController.class)
@Import(SecurityConfig.class)
class CategoryControllerTest {

  private String customerUser;
  private String customerPass;

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private CategoryService categoryService;

  private String adminUser;
  private String adminPass;

  @BeforeEach
  void setUp() {
    adminUser = "admin";
    adminPass = "admin123";
    customerUser = "customer";
    customerPass = "customer123";
  }

  private Category sampleCategory(UUID id, String name) {
    Category c = new Category();
    c.setId(id);
    c.setName(name);
    LocalDateTime now = LocalDateTime.now();
    c.setCreatedAt(now);
    c.setUpdatedAt(now);
    return c;
  }

  @Test
  @DisplayName("GET /categories should return paginated list")
  void list_ok() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Category c1 = sampleCategory(id1, "Electronics");
    Category c2 = sampleCategory(id2, "Appliances");
    Page<Category> page = new PageImpl<>(List.of(c1, c2), PageRequest.of(1, 2), 7);

    when(categoryService.list(eq("elec"), any())).thenReturn(page);

    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/categories")
        .queryParam("page", 1)
        .queryParam("size", 2)
        .queryParam("name", "elec")
        .build())
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.data.totalItems").isEqualTo(7)
      .jsonPath("$.data.totalPages").isEqualTo(page.getTotalPages())
      .jsonPath("$.data.currentPage").isEqualTo(1)
      .jsonPath("$.data.categories[0].id").isEqualTo(id1.toString())
      .jsonPath("$.data.categories[1].id").isEqualTo(id2.toString());
  }

  @Test
  @DisplayName("GET /categories/{id} returns 200 when found")
  void getById_found() {
    UUID id = UUID.randomUUID();
    Category c = sampleCategory(id, "Electronics");
    when(categoryService.getById(id)).thenReturn(Optional.of(c));

    webTestClient.get().uri("/categories/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.name").isEqualTo("Electronics");
  }

  @Test
  @DisplayName("GET /categories/{id} with invalid UUID returns 404 not found")
  void getById_invalidUUID() {
    webTestClient.get().uri("/categories/abc")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("POST /categories with valid body returns 201 and data")
  void create_created() {
    UUID id = UUID.randomUUID();
    Category saved = sampleCategory(id, "Electronics");
    when(categoryService.create(new CategoryDTO("Electronics"))).thenReturn(saved);

    String body = "{\n  \"name\": \"Electronics\"\n}";

    webTestClient.post().uri("/categories")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.name").isEqualTo("Electronics");
  }

  @Test
  @DisplayName("POST /categories with invalid body returns 400 with validation errors")
  void create_invalid_badRequest() {
    String body = "{ }";

    webTestClient.post().uri("/categories")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors").value(arr -> {
        java.util.List<?> list = (java.util.List<?>) arr;
        java.util.List<String> strings = list.stream().map(Object::toString).toList();
        org.assertj.core.api.Assertions.assertThat(strings)
          .contains("name is required");
      });
  }

  @Test
  @DisplayName("PUT /categories/{id} with valid id returns 200 and data")
  void update_ok() {
    UUID id = UUID.randomUUID();
    Category updated = sampleCategory(id, "Updated");
    when(categoryService.update(eq(id), any(CategoryDTO.class))).thenReturn(updated);

    String body = "{\n  \"name\": \"Updated\"\n}";

    webTestClient.put().uri("/categories/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.name").isEqualTo("Updated");
  }

  @Test
  @DisplayName("PUT /categories/{id} with invalid UUID returns 404")
  void update_invalidUUID() {
    String body = "{\n  \"name\": \"Updated\"\n}";

    webTestClient.put().uri("/categories/abc")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("PUT /categories/{id} returns 404 when not found")
  void update_notFound() {
    UUID id = UUID.randomUUID();
    when(categoryService.update(eq(id), any(CategoryDTO.class))).thenReturn(null);

    String body = "{\n  \"name\": \"Updated\"\n}";

    webTestClient.put().uri("/categories/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("DELETE /categories/{id} returns 204 on success")
  void delete_noContent() {
    UUID id = UUID.randomUUID();
    when(categoryService.delete(id)).thenReturn(true);

    webTestClient.delete().uri("/categories/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNoContent()
      .expectBody().isEmpty();
  }

  @Test
  @DisplayName("DELETE /categories/{id} returns 404 when not found")
  void delete_notFound() {
    UUID id = UUID.randomUUID();
    when(categoryService.delete(id)).thenReturn(false);

    webTestClient.delete().uri("/categories/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("GET /categories without auth returns 401 with authentication failed")
  void security_categories_unauthorized_401() {
    webTestClient.get().uri("/categories")
      .exchange()
      .expectStatus().isUnauthorized()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("authentication failed");
  }

  @Test
  @DisplayName("GET /categories with CUSTOMER role returns 403 with access rejected")
  void security_categories_forbidden_403() {
    webTestClient.get().uri("/categories")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isForbidden()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("access rejected");
  }

}
