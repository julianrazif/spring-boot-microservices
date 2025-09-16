package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.ProductUtils;
import com.julian.razif.microservices.api.product.dto.CategoryDTO;
import com.julian.razif.microservices.api.product.service.CategoryService;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping("/categories")
  public ResponseEntity<Map<String, Object>> list(
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

  @GetMapping("/categories/{categoryId}")
  public ResponseEntity<?> getById(
    @PathVariable("categoryId") String categoryId) {

    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();

    return categoryService.getById(id)
      .<ResponseEntity<?>>map(c -> ResponseEntity.ok(Collections.singletonMap("data", c)))
      .orElseGet(ProductUtils::notFound);
  }

  @PostMapping("/categories")
  public ResponseEntity<?> create(
    @Valid @RequestBody CategoryDTO req) {

    Category c = categoryService.create(req);
    return new ResponseEntity<>(Collections.singletonMap("data", c), HttpStatus.CREATED);
  }

  @PutMapping("/categories/{categoryId}")
  @PatchMapping("/categories/{categoryId}")
  public ResponseEntity<?> update(
    @PathVariable("categoryId") String categoryId,
    @RequestBody CategoryDTO req) {

    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();
    Category c = categoryService.update(id, req);
    if (c == null) return notFound();
    return ResponseEntity.ok(Collections.singletonMap("data", c));
  }

  @DeleteMapping("/categories/{categoryId}")
  public ResponseEntity<?> delete(
    @PathVariable("categoryId") String categoryId) {

    UUID id = parseUUID(categoryId);
    if (id == null) return notFound();
    boolean ok = categoryService.delete(id);
    if (!ok) return notFound();
    return ResponseEntity.noContent().build();
  }

}
