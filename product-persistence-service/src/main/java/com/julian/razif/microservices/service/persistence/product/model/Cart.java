package com.julian.razif.microservices.service.persistence.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {

  @Id
  @ColumnDefault("uuid_generate_v4()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @NotNull
  @Column(name = "quantity", nullable = false, precision = 10)
  private BigDecimal quantity;

  @Size(max = 100)
  @NotNull
  @Column(name = "status", nullable = false, length = 100)
  private String status;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

}
