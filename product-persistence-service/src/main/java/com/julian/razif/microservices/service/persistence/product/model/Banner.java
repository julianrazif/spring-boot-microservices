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
@Table(name = "banners")
public class Banner {

  @Id
  @ColumnDefault("uuid_generate_v4()")
  @Column(name = "id", nullable = false)
  private UUID id;

  @Size(max = 100)
  @NotNull
  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @NotNull
  @Column(name = "status", nullable = false)
  private Boolean status = false;

  @Size(max = 10000)
  @NotNull
  @Column(name = "image_url", nullable = false, length = 10000)
  private String imageUrl;

  @Size(max = 100)
  @NotNull
  @Column(name = "discovery", nullable = false, length = 100)
  private String discovery;

  @NotNull
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @NotNull
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

}
