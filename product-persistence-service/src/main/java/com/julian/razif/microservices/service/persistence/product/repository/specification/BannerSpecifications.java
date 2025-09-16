package com.julian.razif.microservices.service.persistence.product.repository.specification;

import com.julian.razif.microservices.service.persistence.product.model.Banner;
import org.springframework.data.jpa.domain.Specification;

public final class BannerSpecifications {

  private BannerSpecifications() {
  }

  public static Specification<Banner> titleContainsIgnoreCase(String title) {
    if (title == null || title.isBlank()) return null;
    String like = "%" + title.trim().toLowerCase() + "%";
    return (root, query, cb) -> cb.like(cb.lower(root.get("title")), like);
  }

  public static Specification<Banner> statusEquals(Boolean status) {
    if (status == null) return null;
    return (root, query, cb) -> cb.equal(root.get("status"), status);
  }

}
