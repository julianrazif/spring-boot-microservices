package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.ProductUtils;
import com.julian.razif.microservices.api.product.dto.ProductDTO;
import com.julian.razif.microservices.api.product.dto.ProductEnvelope;
import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.api.product.service.ProductService;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

  private final ProductMapper productMapper;
  private final ProductService productService;

  @GetMapping("/products")
  public ResponseEntity<Map<String, Object>> list(
    @RequestParam(name = "page", required = false, defaultValue = "0") int page,
    @RequestParam(name = "size", required = false, defaultValue = "10") int size,
    @RequestParam(name = "name", required = false) String name,
    @RequestParam(name = "categoryId", required = false) String categoryIdStr,
    @RequestParam(name = "minPrice", required = false) String minPriceStr,
    @RequestParam(name = "maxPrice", required = false) String maxPriceStr) {

    UUID categoryId = parseUUID(categoryIdStr);
    BigDecimal minPrice = parseBigDecimal(minPriceStr);
    BigDecimal maxPrice = parseBigDecimal(maxPriceStr);

    if (page < 0) page = 0;
    if (size <= 0) size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Page<Product> products = productService.list(name, categoryId, minPrice, maxPrice, pageable);

    List<Map<String, Object>> productDtos = new ArrayList<>();
    for (Product p : products.getContent()) {
      productDtos.add(productMapper.toProductDto(p));
    }

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", products.getTotalElements());
    data.put("products", productDtos);
    data.put("totalPages", products.getTotalPages());
    data.put("currentPage", products.getNumber());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("data", data);
    return ResponseEntity.ok(body);
  }

  @GetMapping("/products/{productId}")
  public ResponseEntity<?> getById(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return notFound();

    return productService.getById(id)
      .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Collections.singletonMap("data", productMapper.toProductDto(p))))
      .orElseGet(ProductUtils::notFound);
  }

  @PostMapping("/products")
  public ResponseEntity<?> create(
    @Valid @RequestBody(required = false) ProductEnvelope payload) {

    ProductDTO req = (payload == null) ? null : payload.product();
    if (req == null) return ResponseEntity.badRequest().body(Map.of("errors", List.of(
      "category is required",
      "product name is required",
      "image URL is required",
      "price is required",
      "stock is required"
    )));

    UUID categoryId = parseUUID(req.categoryId());
    if (categoryId == null) {
      // treat invalid UUID as empty/invalid input
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", List.of("category can not be empty"));
      return ResponseEntity.badRequest().body(body);
    }

    BigDecimal price = parseBigDecimal(req.price());
    BigDecimal stock = parseBigDecimal(req.stock());

    List<String> errors = new ArrayList<>();
    if (price == null) errors.add("price is not valid");
    if (stock == null) errors.add("stock is not valid");
    if (!errors.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    Product p = productService.create(req, categoryId, price, stock);
    if (p == null) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));
    return new ResponseEntity<>(Collections.singletonMap("product", productMapper.toProductDto(p)), HttpStatus.CREATED);
  }

  @PutMapping("/products/{productId}")
  @PatchMapping("/products/{productId}")
  public ResponseEntity<?> update(
    @PathVariable("productId") String productId,
    @Valid @RequestBody(required = false) ProductEnvelope payload) {

    UUID id = parseUUID(productId);
    if (id == null) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));

    ProductDTO req = (payload == null) ? null : payload.product();
    if (req == null) return ResponseEntity.badRequest().body(Map.of("errors", List.of(
      "category is required",
      "product name is required",
      "image URL is required",
      "price is required",
      "stock is required"
    )));

    UUID categoryId = parseUUID(req.categoryId());
    if (categoryId == null) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", List.of("category can not be empty"));
      return ResponseEntity.badRequest().body(body);
    }

    BigDecimal price = parseBigDecimal(req.price());
    BigDecimal stock = parseBigDecimal(req.stock());

    List<String> errors = new ArrayList<>();
    if (price == null) errors.add("price is not valid");
    if (stock == null) errors.add("stock is not valid");
    if (!errors.isEmpty()) {
      return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }

    Product p = productService.update(id, req, categoryId, price, stock);
    if (p == null) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));
    return ResponseEntity.ok(Collections.singletonMap("product", productMapper.toProductDto(p)));
  }

  @DeleteMapping("/products/{productId}")
  public ResponseEntity<?> delete(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));
    boolean ok = productService.delete(id);
    if (!ok) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));
    return ResponseEntity.ok(Collections.singletonMap("message", "product deleted successfully"));
  }

}
