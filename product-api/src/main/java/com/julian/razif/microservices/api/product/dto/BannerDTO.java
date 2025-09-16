package com.julian.razif.microservices.api.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BannerDTO(
  @NotNull(message = "title is required")
  @Pattern(regexp = ".*\\S.*", message = "title can not be empty")
  @Size(min = 5, max = 100, message = "title length must be between 5 and 100")
  String title,

  @NotNull(message = "status is required")
  Boolean status,

  @NotNull(message = "image URL is required")
  @Pattern(regexp = ".*\\S.*", message = "image URL can not be empty")
  String imageUrl,

  @NotNull(message = "discovery is required")
  @Pattern(regexp = ".*\\S.*", message = "discovery can not be empty")
  @Size(min = 5, max = 100, message = "discovery length must be between 5 and 100")
  String discovery) {
}
