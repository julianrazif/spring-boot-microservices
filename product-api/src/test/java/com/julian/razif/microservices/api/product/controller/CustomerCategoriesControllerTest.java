package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.CategoryService;
import com.julian.razif.microservices.api.product.service.ProductService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CustomerController.class)
@Import(SecurityConfig.class)
class CustomerCategoriesControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private CategoryService categoryService;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private ProductMapper productMapper;

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
  @DisplayName("GET /customer/categories returns paginated list without auth")
  void list_ok_public() {
    Category c1 = sampleCategory(UUID.randomUUID(), "Electronics");
    Category c2 = sampleCategory(UUID.randomUUID(), "Appliances");
    Page<Category> page = new PageImpl<>(List.of(c1, c2), PageRequest.of(0, 2), 2);

    when(categoryService.list(eq("e"), any())).thenReturn(page);

    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/customer/categories")
        .queryParam("page", 0)
        .queryParam("size", 2)
        .queryParam("name", "e")
        .build())
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.data.totalItems").isEqualTo(2)
      .jsonPath("$.data.categories[0].name").isEqualTo("Electronics")
      .jsonPath("$.data.categories[1].name").isEqualTo("Appliances");
  }

  @Test
  @DisplayName("GET /customer/categories/{id} returns 200 when found (public)")
  void getById_found_public() {
    UUID id = UUID.randomUUID();
    Category c = sampleCategory(id, "Electronics");
    when(categoryService.getById(id)).thenReturn(Optional.of(c));

    webTestClient.get().uri("/customer/categories/" + id)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.name").isEqualTo("Electronics");
  }

  @Test
  @DisplayName("GET /customer/categories/{id} returns 404 when not found")
  void getById_notFound_public() {
    UUID id = UUID.randomUUID();
    when(categoryService.getById(id)).thenReturn(Optional.empty());

    webTestClient.get().uri("/customer/categories/" + id)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("GET /customer/categories/{id} returns 404 for invalid UUID")
  void getById_invalidUUID_public() {
    webTestClient.get().uri("/customer/categories/abc")
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }
}
