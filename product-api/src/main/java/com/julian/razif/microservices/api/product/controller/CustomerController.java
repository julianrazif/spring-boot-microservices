package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.ProductUtils;
import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.api.product.service.CategoryService;
import com.julian.razif.microservices.api.product.service.ProductService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseBigDecimal;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
@RequiredArgsConstructor
public class CustomerController {

  private final ProductMapper productMapper;
  private final ProductService productService;
  private final CategoryService categoryService;

  @GetMapping("/customer/products")
  public ResponseEntity<Map<String, Object>> listProducts(
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
      Map<String, Object> m = productMapper.toProductDto(p);
      productDtos.add(m);
    }
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", products.getTotalElements());
    data.put("products", productDtos);
    data.put("totalPages", products.getTotalPages());
    data.put("currentPage", products.getNumber());
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/customer/categories")
  public ResponseEntity<Map<String, Object>> listCategories(
    @RequestParam(name = "page", required = false, defaultValue = "0") int page,
    @RequestParam(name = "size", required = false, defaultValue = "10") int size,
    @RequestParam(name = "name", required = false) String name) {

    if (page < 0) page = 0;
    if (size <= 0) size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Page<Category> categories = categoryService.list(name, pageable);
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", categories.getTotalElements());
    data.put("categories", categories.getContent());
    data.put("totalPages", categories.getTotalPages());
    data.put("currentPage", categories.getNumber());
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/customer/products/{productId}")
  public ResponseEntity<?> getProductById(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return ResponseEntity.status(404).body(Map.of("errors", List.of("product not found")));

    return productService.getById(id)
      .<ResponseEntity<?>>map(p -> ResponseEntity.ok(Collections.singletonMap("data", productMapper.toProductDto(p))))
      .orElseGet(() -> ResponseEntity.status(404).body(Map.of("errors", List.of("product not found"))));
  }

  @GetMapping("/customer/categories/{categoryId}")
  public ResponseEntity<?> getCategoryById(
    @PathVariable("categoryId") String categoryId) {

    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();

    return categoryService.getById(id)
      .<ResponseEntity<?>>map(c -> ResponseEntity.ok(Collections.singletonMap("data", c)))
      .orElseGet(ProductUtils::notFound);
  }

}
