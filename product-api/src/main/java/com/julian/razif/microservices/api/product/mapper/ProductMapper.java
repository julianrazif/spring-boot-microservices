package com.julian.razif.microservices.api.product.mapper;

import com.julian.razif.microservices.service.persistence.product.model.Product;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ProductMapper {

  public Map<String, Object> toProductDto(Product p) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", p.getId());
    m.put("CategoryId", p.getCategory() != null ? p.getCategory().getId() : null);
    m.put("name", p.getName());
    m.put("image_url", p.getImageUrl());
    m.put("price", p.getPrice() == null ? null : p.getPrice().toPlainString());
    m.put("stock", p.getStock() == null ? null : p.getStock().stripTrailingZeros().toPlainString());
    m.put("createdAt", p.getCreatedAt());
    m.put("updatedAt", p.getUpdatedAt());
    if (p.getCategory() != null) {
      Map<String, Object> cat = new LinkedHashMap<>();
      cat.put("name", p.getCategory().getName());
      m.put("Category", cat);
    }
    return m;
  }

}
