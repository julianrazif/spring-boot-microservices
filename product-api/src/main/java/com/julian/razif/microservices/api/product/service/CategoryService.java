package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.CategoryDTO;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.repository.CategoryRepository;
import com.julian.razif.microservices.service.persistence.product.repository.specification.CategorySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Page<Category> list(String name, Pageable pageable) {
    Specification<Category> spec = (root, query, cb) -> cb.conjunction();
    Specification<Category> s1 = CategorySpecifications.nameContainsIgnoreCase(name);
    if (s1 != null) spec = spec.and(s1);
    return categoryRepository.findAll(spec, pageable);
  }

  public Optional<Category> getById(UUID id) {
    return categoryRepository.findById(id);
  }

  @Transactional
  public Category create(CategoryDTO req) {
    Category c = new Category();
    c.setId(UUID.randomUUID());
    c.setName(req.name());
    LocalDateTime now = LocalDateTime.now();
    c.setCreatedAt(now);
    c.setUpdatedAt(now);
    return categoryRepository.save(c);
  }

  @Transactional
  public Category update(UUID id, CategoryDTO req) {
    Optional<Category> co = categoryRepository.findById(id);
    if (co.isEmpty()) return null;
    Category c = co.get();
    if (req.name() != null) c.setName(req.name());
    c.setUpdatedAt(LocalDateTime.now());
    return categoryRepository.save(c);
  }

  @Transactional
  public boolean delete(UUID id) {
    Optional<Category> co = categoryRepository.findById(id);
    if (co.isEmpty()) return false;
    categoryRepository.delete(co.get());
    return true;
  }

}
