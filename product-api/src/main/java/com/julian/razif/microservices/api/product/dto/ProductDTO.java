package com.julian.razif.microservices.api.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductDTO(
  @NotNull(message = "category is required")
  @Pattern(regexp = ".*\\S.*", message = "category can not be empty")
  @JsonProperty("CategoryId")
  String categoryId,

  @NotNull(message = "product name is required")
  @Pattern(regexp = ".*\\S.*", message = "product name can not be empty")
  @Size(min = 5, max = 50, message = "product name length must be between 5 and 50")
  String name,

  @NotNull(message = "image URL is required")
  @Pattern(regexp = ".*\\S.*", message = "image URL can not empty")
  @JsonProperty("image_url")
  String imageUrl,

  @NotNull(message = "price is required")
  @Pattern(regexp = ".*\\S.*", message = "price can not be empty")
  String price,

  @NotNull(message = "stock is required")
  @Pattern(regexp = ".*\\S.*", message = "stock can not be empty")
  String stock) {
}
