package com.julian.razif.microservices.service.persistence.product.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product {

  @Id
  @Column(name = "id", nullable = false)
  private UUID id;

  @Size(max = 50)
  @NotNull
  @Column(name = "name", nullable = false, length = 50)
  private String name;

  @Size(max = 10000)
  @NotNull
  @Column(name = "image_url", nullable = false, length = 10000)
  private String imageUrl;

  @NotNull
  @Column(name = "price", nullable = false, precision = 20)
  private BigDecimal price;

  @NotNull
  @Column(name = "stock", nullable = false, precision = 10)
  private BigDecimal stock;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "category_id")
  private Category category;

}
