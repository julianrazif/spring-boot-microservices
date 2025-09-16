package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.CategoryDTO;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
public class CategoryController {

  @PersistenceContext(unitName = "product-pu")
  private EntityManager em;

  @GetMapping("/categories")
  public ResponseEntity<Map<String, Object>> list() {
    List<Category> categories = em.createQuery("select c from Category c", Category.class).getResultList();
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", categories.size());
    data.put("categories", categories);
    data.put("totalPages", 1);
    data.put("currentPage", 0);
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/categories/{categoryId}")
  public ResponseEntity<?> getById(@PathVariable("categoryId") String categoryId) {
    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();
    Category c = em.find(Category.class, id);
    if (c == null) return notFound();
    return ResponseEntity.ok(Collections.singletonMap("data", c));
  }

  @PostMapping("/categories")
  @Transactional
  public ResponseEntity<?> create(@Valid @RequestBody CategoryDTO req) {
    Category c = new Category();
    c.setId(UUID.randomUUID());
    c.setName(req.name());
    LocalDateTime now = LocalDateTime.now();
    c.setCreatedAt(now);
    c.setUpdatedAt(now);
    em.persist(c);
    return new ResponseEntity<>(Collections.singletonMap("data", c), HttpStatus.CREATED);
  }

  @PutMapping("/categories/{categoryId}")
  @PatchMapping("/categories/{categoryId}")
  @Transactional
  public ResponseEntity<?> update(@PathVariable("categoryId") UUID id, @RequestBody CategoryDTO req) {
    Category c = em.find(Category.class, id);
    if (c == null) return notFound();
    if (req.name() != null) c.setName(req.name());
    c.setUpdatedAt(LocalDateTime.now());
    c = em.merge(c);
    return ResponseEntity.ok(Collections.singletonMap("data", c));
  }

  @DeleteMapping("/categories/{categoryId}")
  @Transactional
  public ResponseEntity<?> delete(@PathVariable("categoryId") UUID id) {
    Category c = em.find(Category.class, id);
    if (c == null) return notFound();
    em.remove(c);
    return ResponseEntity.noContent().build();
  }

}
