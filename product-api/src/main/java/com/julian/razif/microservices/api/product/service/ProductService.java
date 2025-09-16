package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.ProductDTO;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import com.julian.razif.microservices.service.persistence.product.repository.CategoryRepository;
import com.julian.razif.microservices.service.persistence.product.repository.ProductRepository;
import com.julian.razif.microservices.service.persistence.product.repository.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;

  public Page<Product> list(String name, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
    Specification<Product> spec = (root, query, cb) -> cb.conjunction();
    Specification<Product> s1 = ProductSpecifications.nameContainsIgnoreCase(name);
    if (s1 != null) spec = spec.and(s1);
    Specification<Product> s2 = ProductSpecifications.categoryIdEquals(categoryId);
    if (s2 != null) spec = spec.and(s2);
    Specification<Product> s3 = ProductSpecifications.priceGte(minPrice);
    if (s3 != null) spec = spec.and(s3);
    Specification<Product> s4 = ProductSpecifications.priceLte(maxPrice);
    if (s4 != null) spec = spec.and(s4);
    return productRepository.findAll(spec, pageable);
  }

  public Optional<Product> getById(UUID id) {
    return productRepository.findById(id);
  }

  @Transactional
  public Product create(ProductDTO req, UUID categoryId, BigDecimal price, BigDecimal stock) {
    Category category = categoryRepository.findById(categoryId).orElse(null);
    if (category == null) {
      return null;
    }
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
    return productRepository.save(p);
  }

  @Transactional
  public Product update(UUID id, ProductDTO req, UUID categoryId, BigDecimal price, BigDecimal stock) {
    Optional<Product> po = productRepository.findById(id);
    if (po.isEmpty()) return null;
    Product p = po.get();

    Category category = categoryRepository.findById(categoryId).orElse(null);
    if (category == null) {
      return null;
    }

    p.setName(req.name().trim());
    p.setImageUrl(req.imageUrl().trim());
    p.setPrice(price);
    p.setStock(stock);
    p.setCategory(category);
    p.setUpdatedAt(LocalDateTime.now());

    return productRepository.save(p);
  }

  @Transactional
  public boolean delete(UUID id) {
    Optional<Product> po = productRepository.findById(id);
    if (po.isEmpty()) return false;
    productRepository.delete(po.get());
    return true;
  }

}
