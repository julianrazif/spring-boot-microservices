package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.BannerDTO;
import com.julian.razif.microservices.service.persistence.product.model.Banner;
import com.julian.razif.microservices.service.persistence.product.repository.BannerRepository;
import com.julian.razif.microservices.service.persistence.product.repository.specification.BannerSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

  private final BannerRepository bannerRepository;

  public Page<Banner> list(String title, Boolean status, Pageable pageable) {
    Specification<Banner> spec = (root, query, cb) -> cb.conjunction();
    Specification<Banner> s1 = BannerSpecifications.titleContainsIgnoreCase(title);
    if (s1 != null) spec = spec.and(s1);
    Specification<Banner> s2 = BannerSpecifications.statusEquals(status);
    if (s2 != null) spec = spec.and(s2);
    return bannerRepository.findAll(spec, pageable);
  }

  public Optional<Banner> getById(UUID id) {
    return bannerRepository.findById(id);
  }

  @Transactional
  public Banner create(BannerDTO req) {
    Banner b = new Banner();
    b.setId(UUID.randomUUID());
    b.setTitle(req.title());
    b.setStatus(Boolean.TRUE.equals(req.status()));
    b.setImageUrl(req.imageUrl());
    b.setDiscovery(req.discovery());
    LocalDateTime now = LocalDateTime.now();
    b.setCreatedAt(now);
    b.setUpdatedAt(now);
    return bannerRepository.save(b);
  }

  @Transactional
  public Banner update(UUID id, BannerDTO req) {
    Optional<Banner> bo = bannerRepository.findById(id);
    if (bo.isEmpty()) return null;
    Banner b = bo.get();
    if (req.title() != null) b.setTitle(req.title());
    if (req.status() != null) b.setStatus(req.status());
    if (req.imageUrl() != null) b.setImageUrl(req.imageUrl());
    if (req.discovery() != null) b.setDiscovery(req.discovery());
    b.setUpdatedAt(LocalDateTime.now());
    return bannerRepository.save(b);
  }

  @Transactional
  public boolean delete(UUID id) {
    Optional<Banner> bo = bannerRepository.findById(id);
    if (bo.isEmpty()) return false;
    bannerRepository.delete(bo.get());
    return true;
  }

}
