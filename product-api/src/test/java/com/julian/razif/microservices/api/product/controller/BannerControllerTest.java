package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.BannerDTO;
import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.BannerService;
import com.julian.razif.microservices.service.persistence.product.model.Banner;
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

@WebFluxTest(controllers = BannerController.class)
@Import(SecurityConfig.class)
class BannerControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private BannerService bannerService;

  private String adminUser;
  private String adminPass;

  @BeforeEach
  void setUp() {
    adminUser = "admin";
    adminPass = "admin123";
  }

  private Banner sampleBanner(UUID id) {
    Banner b = new Banner();
    b.setId(id);
    b.setTitle("Big Sale");
    b.setStatus(true);
    b.setImageUrl("http://img/banner");
    b.setDiscovery("Up to 50% off");
    LocalDateTime now = LocalDateTime.now();
    b.setCreatedAt(now);
    b.setUpdatedAt(now);
    return b;
  }

  @Test
  @DisplayName("GET /banners should return paginated list")
  void list_ok() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Page<Banner> page = new PageImpl<>(List.of(sampleBanner(id1), sampleBanner(id2)), PageRequest.of(1, 2), 5);

    when(bannerService.list(eq("sale"), eq(true), any())).thenReturn(page);

    webTestClient.get()
      .uri(uriBuilder -> uriBuilder.path("/banners")
        .queryParam("page", 1)
        .queryParam("size", 2)
        .queryParam("title", "sale")
        .queryParam("status", true)
        .build())
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.data.totalItems").isEqualTo(5)
      .jsonPath("$.data.totalPages").isEqualTo(page.getTotalPages())
      .jsonPath("$.data.currentPage").isEqualTo(1)
      .jsonPath("$.data.banners[0].id").isEqualTo(id1.toString())
      .jsonPath("$.data.banners[1].id").isEqualTo(id2.toString());
  }

  @Test
  @DisplayName("GET /banners/{id} returns 200 when found")
  void getById_found() {
    UUID id = UUID.randomUUID();
    Banner b = sampleBanner(id);
    when(bannerService.getById(id)).thenReturn(Optional.of(b));

    webTestClient.get().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.title").isEqualTo("Big Sale")
      .jsonPath("$.data.status").isEqualTo(true);
  }

  @Test
  @DisplayName("GET /banners/{id} with invalid UUID returns 404")
  void getById_invalidUUID() {
    webTestClient.get().uri("/banners/abc")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("GET /banners/{id} returns 404 when not found")
  void getById_notFound() {
    UUID id = UUID.randomUUID();
    when(bannerService.getById(id)).thenReturn(Optional.empty());

    webTestClient.get().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("POST /banners with valid body returns 201 and data")
  void create_created() {
    UUID id = UUID.randomUUID();
    Banner saved = sampleBanner(id);
    when(bannerService.create(new BannerDTO("Big Sale", true, "http://img/banner", "Up to 50% off"))).thenReturn(saved);

    String body = """
      {
        "title": "Big Sale",
        "status": true,
        "imageUrl": "http://img/banner",
        "discovery": "Up to 50% off"
      }""";

    webTestClient.post().uri("/banners")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.title").isEqualTo("Big Sale");
  }

  @Test
  @DisplayName("POST /banners with invalid body returns 400 with required messages")
  void create_invalid_badRequest() {
    String body = "{}";

    webTestClient.post().uri("/banners")
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
          .contains("title is required", "status is required", "image URL is required", "discovery is required");
      });
  }

  @Test
  @DisplayName("PUT /banners/{id} returns 200 when updated")
  void update_ok() {
    UUID id = UUID.randomUUID();
    Banner updated = sampleBanner(id);
    updated.setTitle("Hot Deals");
    when(bannerService.update(eq(id), any(BannerDTO.class))).thenReturn(updated);

    String body = """
      {
        "title": "Hot Deals"
      }""";

    webTestClient.put().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.title").isEqualTo("Hot Deals");
  }

  @Test
  @DisplayName("PUT /banners/{id} with invalid UUID returns 404")
  void update_invalidUUID() {
    String body = "{\n  \"title\": \"Hot Deals\"\n}";

    webTestClient.put().uri("/banners/abc")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("PUT /banners/{id} returns 404 when not found")
  void update_notFound() {
    UUID id = UUID.randomUUID();
    when(bannerService.update(eq(id), any(BannerDTO.class))).thenReturn(null);

    String body = "{\n  \"title\": \"Hot Deals\"\n}";

    webTestClient.put().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }

  @Test
  @DisplayName("DELETE /banners/{id} returns 204 on success")
  void delete_noContent() {
    UUID id = UUID.randomUUID();
    when(bannerService.delete(id)).thenReturn(true);

    webTestClient.delete().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNoContent()
      .expectBody().isEmpty();
  }

  @Test
  @DisplayName("DELETE /banners/{id} returns 404 when not found")
  void delete_notFound() {
    UUID id = UUID.randomUUID();
    when(bannerService.delete(id)).thenReturn(false);

    webTestClient.delete().uri("/banners/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("not found");
  }
}
