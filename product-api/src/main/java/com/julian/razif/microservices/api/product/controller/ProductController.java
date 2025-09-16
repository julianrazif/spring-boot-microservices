package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.ProductDTO;
import com.julian.razif.microservices.api.product.dto.ProductEnvelope;
import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

  @PersistenceContext(unitName = "product-pu")
  private EntityManager em;

  private final ProductMapper productMapper;

  @GetMapping("/products")
  public ResponseEntity<Map<String, Object>> list() {
    List<Product> products = em.createQuery("select p from Product p", Product.class).getResultList();

    List<Map<String, Object>> productDtos = new ArrayList<>();
    for (Product p : products) {
      productDtos.add(productMapper.toProductDto(p));
    }

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", products.size());
    data.put("products", productDtos);
    data.put("totalPages", 1);
    data.put("currentPage", 0);

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("data", data);
    return ResponseEntity.ok(body);
  }

  @GetMapping("/products/{productId}")
  public ResponseEntity<?> getById(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return notFound();
    Product product = em.find(Product.class, id);
    if (product == null) {
      return notFound();
    }
    return ResponseEntity.ok(Collections.singletonMap("data", productMapper.toProductDto(product)));
  }

  @PostMapping("/products")
  @Transactional
  public ResponseEntity<?> create(
    @Valid @RequestBody(required = false) ProductEnvelope payload) {

    ProductDTO req = (payload == null) ? null : payload.product();
    if (req == null) return notFound();

    UUID categoryId = parseUUID(req.categoryId());
    if (categoryId == null) {
      // treat invalid UUID as empty/invalid input
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", List.of("category can not be empty"));
      return ResponseEntity.badRequest().body(body);
    }

    Category category = em.find(Category.class, categoryId);
    if (category == null) return notFound();

    BigDecimal price = parseBigDecimal(req.price());
    BigDecimal stock = parseBigDecimal(req.stock());

    Product p = new Product();
    p.setId(UUID.randomUUID());
    p.setName(req.name().trim());
    p.setImageUrl(req.imageUrl().trim());
    p.setPrice(price);
    p.setStock(stock);
    p.setCategory(category);
    LocalDateTime now = LocalDateTime.now();
    p.setCreatedAt(now);
    p.setUpdatedAt(now);

    em.persist(p);
    return new ResponseEntity<>(Collections.singletonMap("product", productMapper.toProductDto(p)), HttpStatus.CREATED);
  }

  @PutMapping("/products/{productId}")
  @PatchMapping("/products/{productId}")
  @Transactional
  public ResponseEntity<?> update(
    @PathVariable("productId") String productId,
    @Valid @RequestBody(required = false) ProductEnvelope payload) {

    UUID id = parseUUID(productId);
    if (id == null) return notFound();
    Product p = em.find(Product.class, id);
    if (p == null) return notFound();

    ProductDTO req = (payload == null) ? null : payload.product();
    if (req == null) return notFound();

    UUID categoryId = parseUUID(req.categoryId());
    if (categoryId == null) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", List.of("category can not be empty"));
      return ResponseEntity.badRequest().body(body);
    }

    Category category = em.find(Category.class, categoryId);
    if (category == null) return notFound();

    BigDecimal price = parseBigDecimal(req.price());
    BigDecimal stock = parseBigDecimal(req.stock());

    p.setName(req.name().trim());
    p.setImageUrl(req.imageUrl().trim());
    p.setPrice(price);
    p.setStock(stock);
    p.setCategory(category);
    p.setUpdatedAt(LocalDateTime.now());

    p = em.merge(p);
    return ResponseEntity.ok(Collections.singletonMap("product", productMapper.toProductDto(p)));
  }

  @DeleteMapping("/products/{productId}")
  @Transactional
  public ResponseEntity<?> delete(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return notFound();
    Product p = em.find(Product.class, id);
    if (p == null) return notFound();
    em.remove(p);
    return ResponseEntity.ok(Collections.singletonMap("message", "product deleted successfully"));
  }

}
