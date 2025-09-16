package com.julian.razif.microservices.api.product.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryDTO(
  @NotNull(message = "name is required")
  @Pattern(regexp = ".*\\S.*", message = "name can not be empty")
  @Size(min = 5, max = 100, message = "name length must be between 5 and 100")
  String name) {
}
