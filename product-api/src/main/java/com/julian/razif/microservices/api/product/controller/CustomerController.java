package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.mapper.ProductMapper;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
@RequiredArgsConstructor
public class CustomerController {

  @PersistenceContext(unitName = "product-pu")
  private EntityManager em;

  private final ProductMapper productMapper;

  @GetMapping("/customer/products")
  public ResponseEntity<Map<String, Object>> listProducts() {
    List<Product> products = em.createQuery("select p from Product p", Product.class).getResultList();
    List<Map<String, Object>> productDtos = new ArrayList<>();
    for (Product p : products) {
      Map<String, Object> m = productMapper.toProductDto(p);
      productDtos.add(m);
    }
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", products.size());
    data.put("products", productDtos);
    data.put("totalPages", 1);
    data.put("currentPage", 0);
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/customer/categories")
  public ResponseEntity<Map<String, Object>> listCategories() {
    List<Category> categories = em.createQuery("select c from Category c", Category.class).getResultList();
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", categories.size());
    data.put("categories", categories);
    data.put("totalPages", 1);
    data.put("currentPage", 0);
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/customer/products/{productId}")
  public ResponseEntity<?> getProductById(
    @PathVariable("productId") String productId) {

    UUID id = parseUUID(productId);
    if (id == null) return notFound();
    Product p = em.find(Product.class, id);
    if (p == null) return notFound();
    Map<String, Object> m = productMapper.toProductDto(p);
    return ResponseEntity.ok(Collections.singletonMap("data", m));
  }

  @GetMapping("/customer/categories/{categoryId}")
  public ResponseEntity<?> getCategoryById(
    @PathVariable("categoryId") String categoryId) {

    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();
    Category c = em.find(Category.class, id);
    if (c == null) return notFound();
    return ResponseEntity.ok(Collections.singletonMap("data", c));
  }

}
