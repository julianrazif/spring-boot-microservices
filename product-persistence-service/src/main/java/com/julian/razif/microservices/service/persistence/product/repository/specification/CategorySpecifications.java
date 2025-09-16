package com.julian.razif.microservices.service.persistence.product.repository.specification;

import com.julian.razif.microservices.service.persistence.product.model.Category;
import org.springframework.data.jpa.domain.Specification;

public final class CategorySpecifications {

  private CategorySpecifications() {
  }

  public static Specification<Category> nameContainsIgnoreCase(String name) {
    if (name == null || name.isBlank()) return null;
    String like = "%" + name.trim().toLowerCase() + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get("name")), like);
  }

}
