package com.julian.razif.microservices.service.persistence.product.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

  @Id
  @ColumnDefault("uuid_generate_v4()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @Size(max = 50)
  @NotNull
  @Column(name = "display_name", nullable = false, length = 50)
  private String displayName;

  @Size(max = 50)
  @NotNull
  @Column(name = "email", nullable = false, length = 50, unique = true)
  private String email;

  @Size(max = 10000)
  @NotNull
  @Column(name = "password", nullable = false, length = 10000)
  private String password;

  @Size(max = 50)
  @NotNull
  @ColumnDefault("'customer'")
  @Column(name = "role", nullable = false, length = 50)
  private String role;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

}
