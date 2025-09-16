package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.ProductDTO;
import com.julian.razif.microservices.api.product.dto.ProductEnvelope;
import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.ProductService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerTest {

  private String customerUser;
  private String customerPass;

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private ProductMapper productMapper;

  private String adminUser;
  private String adminPass;

  @BeforeEach
  void setUp() {
    adminUser = "admin";
    adminPass = "admin123";
    customerUser = "customer";
    customerPass = "customer123";
  }

  private Product sampleProduct(UUID id, UUID categoryId) {
    Category cat = new Category();
    cat.setId(categoryId);
    cat.setName("Electronics");
    Product p = new Product();
    p.setId(id);
    p.setName("Phone XYZ");
    p.setImageUrl("http://img/xyz");
    p.setPrice(new BigDecimal("1200000"));
    p.setStock(new BigDecimal("5"));
    p.setCategory(cat);
    p.setCreatedAt(LocalDateTime.now());
    p.setUpdatedAt(LocalDateTime.now());
    return p;
  }

  private Map<String, Object> sampleProductDto(UUID id, UUID categoryId) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", id);
    m.put("CategoryId", categoryId);
    m.put("name", "Phone XYZ");
    m.put("image_url", "http://img/xyz");
    m.put("price", "1200000");
    m.put("stock", "5");
    Map<String, Object> cat = new LinkedHashMap<>();
    cat.put("name", "Electronics");
    m.put("Category", cat);
    return m;
  }

  @Test
  @DisplayName("GET /products should return paginated list with mapped products")
  void listProducts_ok() {
    UUID categoryId = UUID.randomUUID();
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    Product p1 = sampleProduct(id1, categoryId);
    Product p2 = sampleProduct(id2, categoryId);
    Page<Product> page = new PageImpl<>(List.of(p1, p2), PageRequest.of(1, 2), 7);

    when(productService.list(eq("pho"), eq(categoryId), eq(new BigDecimal("10")), eq(new BigDecimal("20")), any()))
      .thenReturn(page);
    when(productMapper.toProductDto(p1)).thenReturn(sampleProductDto(id1, categoryId));
    when(productMapper.toProductDto(p2)).thenReturn(sampleProductDto(id2, categoryId));

    webTestClient
      .get()
      .uri(uriBuilder -> uriBuilder.path("/products")
        .queryParam("page", 1)
        .queryParam("size", 2)
        .queryParam("name", "pho")
        .queryParam("categoryId", categoryId.toString())
        .queryParam("minPrice", "10")
        .queryParam("maxPrice", "20")
        .build())
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
      .expectBody()
      .jsonPath("$.data.totalItems").isEqualTo(7)
      .jsonPath("$.data.totalPages").isEqualTo(page.getTotalPages())
      .jsonPath("$.data.currentPage").isEqualTo(1)
      .jsonPath("$.data.products[0].id").isEqualTo(id1.toString())
      .jsonPath("$.data.products[1].id").isEqualTo(id2.toString());
  }

  @Test
  @DisplayName("GET /products/{id} returns 200 with product when found")
  void getById_found() {
    UUID id = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();
    Product p = sampleProduct(id, categoryId);
    when(productService.getById(id)).thenReturn(Optional.of(p));
    when(productMapper.toProductDto(p)).thenReturn(sampleProductDto(id, categoryId));

    webTestClient.get().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.CategoryId").isEqualTo(categoryId.toString());
  }

  @Test
  @DisplayName("GET /products/{id} returns 404 when not found")
  void getById_notFound() {
    UUID id = UUID.randomUUID();
    when(productService.getById(id)).thenReturn(Optional.empty());

    webTestClient.get().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("product not found");
  }

  @Test
  @DisplayName("POST /products with null body returns 400 with required field messages")
  void create_nullBody_badRequest() {
    webTestClient.post().uri("/products")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors").isArray()
      .jsonPath("$.errors").value(arr -> {
        // expect 5 error messages
        org.assertj.core.api.Assertions.assertThat(((List<?>) arr)).hasSize(5);
      });
  }

  @Test
  @DisplayName("POST /products with invalid CategoryId returns 400 category can not be empty")
  void create_invalidCategory_badRequest() {
    String body = """
      {
        "product": {
          "CategoryId": "abc",
          "name": "Phone XYZ",
          "image_url": "http://img/xyz",
          "price": "120",
          "stock": "5"
        }
      }""";

    webTestClient.post().uri("/products")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("category can not be empty");
  }

  @Test
  @DisplayName("POST /products with non-numeric price/stock returns 400 with validation messages")
  void create_invalidPriceStock_badRequest() {
    UUID categoryId = UUID.randomUUID();
    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + categoryId + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"abc\",\n" +
      "    \"stock\": \"x\"\n" +
      "  }\n" +
      "}";

    webTestClient.post().uri("/products")
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
          .contains("price is not valid", "stock is not valid");
      });
  }

  @Test
  @DisplayName("POST /products returns 201 with product when created")
  void create_created() {
    UUID categoryId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();

    ProductDTO dto = new ProductDTO(categoryId.toString(), "Phone XYZ", "http://img/xyz", "1200000", "5");
    ProductEnvelope envelope = new ProductEnvelope(dto);

    Product saved = sampleProduct(productId, categoryId);
    when(productService.create(any(ProductDTO.class), eq(categoryId), eq(new BigDecimal("1200000")), eq(new BigDecimal("5"))))
      .thenReturn(saved);
    when(productMapper.toProductDto(saved)).thenReturn(sampleProductDto(productId, categoryId));

    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + categoryId + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"1200000\",\n" +
      "    \"stock\": \"5\"\n" +
      "  }\n" +
      "}";

    webTestClient.post().uri("/products")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isCreated()
      .expectBody()
      .jsonPath("$.product.id").isEqualTo(productId.toString())
      .jsonPath("$.product.CategoryId").isEqualTo(categoryId.toString());
  }

  @Test
  @DisplayName("DELETE /products/{id} returns 200 on success")
  void delete_ok() {
    UUID id = UUID.randomUUID();
    when(productService.delete(id)).thenReturn(true);

    webTestClient.delete().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.message").isEqualTo("product deleted successfully");
  }

  @Test
  @DisplayName("DELETE /products/{id} returns 404 when not found")
  void delete_notFound() {
    UUID id = UUID.randomUUID();
    when(productService.delete(id)).thenReturn(false);

    webTestClient.delete().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("product not found");
  }

  @Test
  @DisplayName("PUT /products/{id} returns 200 with product when updated")
  void update_put_ok() {
    UUID productId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Product updated = sampleProduct(productId, categoryId);
    when(productService.update(eq(productId), any(ProductDTO.class), eq(categoryId), eq(new java.math.BigDecimal("1200000")), eq(new java.math.BigDecimal("5"))))
      .thenReturn(updated);
    when(productMapper.toProductDto(updated)).thenReturn(sampleProductDto(productId, categoryId));

    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + categoryId + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"1200000\",\n" +
      "    \"stock\": \"5\"\n" +
      "  }\n" +
      "}";

    webTestClient.put().uri("/products/" + productId)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.product.id").isEqualTo(productId.toString())
      .jsonPath("$.product.CategoryId").isEqualTo(categoryId.toString());
  }

  @Test
  @DisplayName("PUT /products/{id} with invalid UUID returns 404 product not found")
  void update_put_invalidUUID() {
    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + java.util.UUID.randomUUID() + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"1200000\",\n" +
      "    \"stock\": \"5\"\n" +
      "  }\n" +
      "}";

    webTestClient.put().uri("/products/abc")
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("product not found");
  }

  @Test
  @DisplayName("PUT /products/{id} with null body returns 400 with required field messages")
  void update_put_nullBody_badRequest() {
    UUID id = UUID.randomUUID();

    webTestClient.put().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors").isArray()
      .jsonPath("$.errors").value(arr -> org.assertj.core.api.Assertions.assertThat(((List<?>) arr)).hasSize(5));
  }

  @Test
  @DisplayName("PUT /products/{id} with invalid CategoryId returns 400 category can not be empty")
  void update_put_invalidCategory_badRequest() {
    UUID id = UUID.randomUUID();
    String body = """
      {
        "product": {
          "CategoryId": "abc",
          "name": "Phone XYZ",
          "image_url": "http://img/xyz",
          "price": "1200000",
          "stock": "5"
        }
      }""";

    webTestClient.put().uri("/products/" + id)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isBadRequest()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("category can not be empty");
  }

  @Test
  @DisplayName("PUT /products/{id} with non-numeric price/stock returns 400 with messages")
  void update_put_invalidPriceStock_badRequest() {
    UUID id = UUID.randomUUID();
    UUID categoryId = java.util.UUID.randomUUID();
    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + categoryId + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"abc\",\n" +
      "    \"stock\": \"x\"\n" +
      "  }\n" +
      "}";

    webTestClient.put().uri("/products/" + id)
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
          .contains("price is not valid", "stock is not valid");
      });
  }

  @Test
  @DisplayName("PATCH /products/{id} returns 200 with product when updated")
  void update_patch_ok() {
    UUID productId = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();

    Product updated = sampleProduct(productId, categoryId);
    when(productService.update(eq(productId), any(ProductDTO.class), eq(categoryId), eq(new java.math.BigDecimal("1200000")), eq(new java.math.BigDecimal("5"))))
      .thenReturn(updated);
    when(productMapper.toProductDto(updated)).thenReturn(sampleProductDto(productId, categoryId));

    String body = "{\n" +
      "  \"product\": {\n" +
      "    \"CategoryId\": \"" + categoryId + "\",\n" +
      "    \"name\": \"Phone XYZ\",\n" +
      "    \"image_url\": \"http://img/xyz\",\n" +
      "    \"price\": \"1200000\",\n" +
      "    \"stock\": \"5\"\n" +
      "  }\n" +
      "}";

    webTestClient.patch().uri("/products/" + productId)
      .headers(h -> h.setBasicAuth(adminUser, adminPass))
      .contentType(MediaType.APPLICATION_JSON)
      .bodyValue(body)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.product.id").isEqualTo(productId.toString())
      .jsonPath("$.product.CategoryId").isEqualTo(categoryId.toString());
  }

  @Test
  @DisplayName("GET /products without auth returns 401 with authentication failed")
  void security_unauthorized_401() {
    webTestClient.get().uri("/products")
      .exchange()
      .expectStatus().isUnauthorized()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("authentication failed");
  }

  @Test
  @DisplayName("GET /products with CUSTOMER role returns 403 with access rejected")
  void security_forbidden_403() {
    webTestClient.get().uri("/products")
      .headers(h -> h.setBasicAuth(customerUser, customerPass))
      .exchange()
      .expectStatus().isForbidden()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("access rejected");
  }

}
