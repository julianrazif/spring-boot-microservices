package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.BannerDTO;
import com.julian.razif.microservices.service.persistence.product.model.Banner;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
public class BannerController {

  @PersistenceContext(unitName = "product-pu")
  private EntityManager em;

  @GetMapping("/banners")
  public ResponseEntity<Map<String, Object>> list() {
    List<Banner> banners = em.createQuery("select b from Banner b", Banner.class).getResultList();
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", banners.size());
    data.put("banners", banners);
    data.put("totalPages", 1);
    data.put("currentPage", 0);
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/banners/{bannerId}")
  public ResponseEntity<?> getById(
    @PathVariable("bannerId") String bannerId) {

    UUID id = parseUUID(bannerId);
    if (id == null) return notFound();
    Banner b = em.find(Banner.class, id);
    if (b == null) return notFound();
    return ResponseEntity.ok(Collections.singletonMap("data", b));
  }

  @PostMapping("/banners")
  @Transactional
  public ResponseEntity<?> create(
    @Valid @RequestBody BannerDTO req) {

    Banner b = new Banner();
    b.setId(UUID.randomUUID());
    b.setTitle(req.title());
    b.setStatus(Boolean.TRUE.equals(req.status()));
    b.setImageUrl(req.imageUrl());
    b.setDiscovery(req.discovery());
    LocalDateTime now = LocalDateTime.now();
    b.setCreatedAt(now);
    b.setUpdatedAt(now);
    em.persist(b);
    return new ResponseEntity<>(Collections.singletonMap("data", b), HttpStatus.CREATED);
  }

  @PutMapping("/banners/{bannerId}")
  @PatchMapping("/banners/{bannerId}")
  @Transactional
  public ResponseEntity<?> update(
    @PathVariable("bannerId") UUID id,
    @RequestBody BannerDTO req) {

    Banner b = em.find(Banner.class, id);
    if (b == null) return notFound();
    if (req.title() != null) b.setTitle(req.title());
    if (req.status() != null) b.setStatus(req.status());
    if (req.imageUrl() != null) b.setImageUrl(req.imageUrl());
    if (req.discovery() != null) b.setDiscovery(req.discovery());
    b.setUpdatedAt(LocalDateTime.now());
    b = em.merge(b);
    return ResponseEntity.ok(Collections.singletonMap("data", b));
  }

  @DeleteMapping("/banners/{bannerId}")
  @Transactional
  public ResponseEntity<?> delete(
    @PathVariable("bannerId") UUID id) {

    Banner b = em.find(Banner.class, id);
    if (b == null) return notFound();
    em.remove(b);
    return ResponseEntity.noContent().build();
  }
  
}
