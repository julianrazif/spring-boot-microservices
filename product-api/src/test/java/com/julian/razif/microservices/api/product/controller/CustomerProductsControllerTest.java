package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.api.product.security.SecurityConfig;
import com.julian.razif.microservices.api.product.service.CategoryService;
import com.julian.razif.microservices.api.product.service.ProductService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
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

@WebFluxTest(controllers = CustomerController.class)
@Import(SecurityConfig.class)
class CustomerProductsControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private ProductService productService;

  @MockitoBean
  private CategoryService categoryService;

  @MockitoBean
  private ProductMapper productMapper;

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
  @DisplayName("GET /customer/products should return paginated list without auth")
  void list_ok_public() {
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
      .uri(uriBuilder -> uriBuilder.path("/customer/products")
        .queryParam("page", 1)
        .queryParam("size", 2)
        .queryParam("name", "pho")
        .queryParam("categoryId", categoryId.toString())
        .queryParam("minPrice", "10")
        .queryParam("maxPrice", "20")
        .build())
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
  @DisplayName("GET /customer/products/{id} returns 200 with product (public)")
  void getById_found_public() {
    UUID id = UUID.randomUUID();
    UUID categoryId = UUID.randomUUID();
    Product p = sampleProduct(id, categoryId);
    when(productService.getById(id)).thenReturn(Optional.of(p));
    when(productMapper.toProductDto(p)).thenReturn(sampleProductDto(id, categoryId));

    webTestClient.get().uri("/customer/products/" + id)
      .exchange()
      .expectStatus().isOk()
      .expectBody()
      .jsonPath("$.data.id").isEqualTo(id.toString())
      .jsonPath("$.data.CategoryId").isEqualTo(categoryId.toString());
  }

  @Test
  @DisplayName("GET /customer/products/{id} returns 404 when not found")
  void getById_notFound_public() {
    UUID id = UUID.randomUUID();
    when(productService.getById(id)).thenReturn(Optional.empty());

    webTestClient.get().uri("/customer/products/" + id)
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("product not found");
  }

  @Test
  @DisplayName("GET /customer/products/{id} returns 404 for invalid UUID")
  void getById_invalidUUID_public() {
    webTestClient.get().uri("/customer/products/abc")
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.errors[0]").isEqualTo("product not found");
  }
}
