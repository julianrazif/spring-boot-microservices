package com.julian.razif.microservices.api.product.dto;

import jakarta.validation.Valid;

public record ProductEnvelope(
  @Valid
  ProductDTO product) {
}
