package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.ProductUtils;
import com.julian.razif.microservices.api.product.dto.BannerDTO;
import com.julian.razif.microservices.api.product.service.BannerService;
import com.julian.razif.microservices.service.persistence.product.model.Banner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.julian.razif.microservices.api.product.ProductUtils.notFound;
import static com.julian.razif.microservices.api.product.ProductUtils.parseUUID;

@RestController
@RequiredArgsConstructor
public class BannerController {

  private final BannerService bannerService;

  @GetMapping("/banners")
  public ResponseEntity<Map<String, Object>> list(
    @RequestParam(name = "page", required = false, defaultValue = "0") int page,
    @RequestParam(name = "size", required = false, defaultValue = "10") int size,
    @RequestParam(name = "title", required = false) String title,
    @RequestParam(name = "status", required = false) Boolean status) {

    if (page < 0) page = 0;
    if (size <= 0) size = 10;
    Pageable pageable = PageRequest.of(page, size);

    Page<Banner> banners = bannerService.list(title, status, pageable);

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("totalItems", banners.getTotalElements());
    data.put("banners", banners.getContent());
    data.put("totalPages", banners.getTotalPages());
    data.put("currentPage", banners.getNumber());
    return ResponseEntity.ok(Collections.singletonMap("data", data));
  }

  @GetMapping("/banners/{bannerId}")
  public ResponseEntity<?> getById(
    @PathVariable("bannerId") String bannerId) {

    UUID id = parseUUID(bannerId);
    if (id == null) return notFound();

    return bannerService.getById(id)
      .<ResponseEntity<?>>map(b -> ResponseEntity.ok(Collections.singletonMap("data", b)))
      .orElseGet(ProductUtils::notFound);
  }

  @PostMapping("/banners")
  public ResponseEntity<?> create(
    @Valid @RequestBody BannerDTO req) {

    Banner b = bannerService.create(req);
    return new ResponseEntity<>(Collections.singletonMap("data", b), HttpStatus.CREATED);
  }

  @PutMapping("/banners/{bannerId}")
  @PatchMapping("/banners/{bannerId}")
  public ResponseEntity<?> update(
    @PathVariable("bannerId") String bannerId,
    @RequestBody BannerDTO req) {

    UUID id = parseUUID(bannerId);
    if (id == null) return notFound();
    Banner b = bannerService.update(id, req);
    if (b == null) return notFound();
    return ResponseEntity.ok(Collections.singletonMap("data", b));
  }

  @DeleteMapping("/banners/{bannerId}")
  public ResponseEntity<?> delete(
    @PathVariable("bannerId") String bannerId) {

    UUID id = parseUUID(bannerId);
    if (id == null) return notFound();
    boolean ok = bannerService.delete(id);
    if (!ok) return notFound();
    return ResponseEntity.noContent().build();
  }

}
