package com.julian.razif.microservices.api.product.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
  @NotNull(message = "name is required")
  @Pattern(regexp = ".*\\S.*", message = "name can not be empty")
  @Size(min = 5, max = 50, message = "name length must be between 5 and 50")
  String displayName,

  @NotNull(message = "email is required")
  @Pattern(regexp = ".*\\S.*", message = "email can not be empty")
  @Email(message = "email format is not valid")
  String email,

  @NotNull(message = "password is required")
  @Pattern(regexp = ".*\\S.*", message = "password can not be empty")
  @Size(min = 4, message = "at least password length is 4")
  String password,

  String role) {
}
