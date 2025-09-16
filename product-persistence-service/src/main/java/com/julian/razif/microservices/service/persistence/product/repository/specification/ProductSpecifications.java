package com.julian.razif.microservices.service.persistence.product.repository.specification;

import com.julian.razif.microservices.service.persistence.product.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public final class ProductSpecifications {

  private ProductSpecifications() {
  }

  public static Specification<Product> nameContainsIgnoreCase(String name) {
    if (name == null || name.isBlank()) return null;
    String like = "%" + name.trim().toLowerCase() + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get("name")), like);
  }

  public static Specification<Product> categoryIdEquals(UUID categoryId) {
    if (categoryId == null) return null;
    return (root, query, cb) -> cb.equal(root.join("category").get("id"), categoryId);
  }

  public static Specification<Product> priceGte(BigDecimal min) {
    if (min == null) return null;
    return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), min);
  }

  public static Specification<Product> priceLte(BigDecimal max) {
    if (max == null) return null;
    return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), max);
  }

}
