package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.BannerDTO;
import com.julian.razif.microservices.service.persistence.product.model.Banner;
import com.julian.razif.microservices.service.persistence.product.repository.BannerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BannerServiceTest {

  @Mock
  BannerRepository bannerRepository;

  @InjectMocks
  BannerService bannerService;

  @Captor
  ArgumentCaptor<Banner> bannerCaptor;

  private Banner sampleBanner(UUID id, String title, boolean status) {
    Banner b = new Banner();
    b.setId(id);
    b.setTitle(title);
    b.setStatus(status);
    b.setImageUrl("http://img");
    b.setDiscovery("disc");
    b.setCreatedAt(LocalDateTime.now().minusDays(1));
    b.setUpdatedAt(LocalDateTime.now().minusHours(3));
    return b;
  }

  @Test
  @DisplayName("list applies title and status filters and delegates to repository")
  void list_withFilters() {
    Pageable pageable = PageRequest.of(0, 5);
    Banner existing = sampleBanner(UUID.randomUUID(), "Sale", true);
    Page<Banner> page = new PageImpl<>(List.of(existing), pageable, 1);
    when(bannerRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<Banner> result = bannerService.list("Sa", true, pageable);

    assertThat(result.getContent()).containsExactly(existing);
    verify(bannerRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("getById delegates to repository")
  void getById_delegates() {
    UUID id = UUID.randomUUID();
    Banner existing = sampleBanner(id, "B1", true);
    when(bannerRepository.findById(id)).thenReturn(Optional.of(existing));

    Optional<Banner> found = bannerService.getById(id);
    assertThat(found).containsSame(existing);
    verify(bannerRepository).findById(id);
  }

  @Test
  @DisplayName("create copies fields, sets id and timestamps, defaults status when null")
  void create_success_andDefaultStatus() {
    when(bannerRepository.save(any(Banner.class))).thenAnswer(inv -> inv.getArgument(0));

    // When status true
    BannerDTO dtoTrue = new BannerDTO("Title A", true, "http://x", "disc a");
    Banner savedTrue = bannerService.create(dtoTrue);
    assertThat(savedTrue.getId()).isNotNull();
    assertThat(savedTrue.getTitle()).isEqualTo("Title A");
    assertThat(savedTrue.getStatus()).isTrue();
    assertThat(savedTrue.getImageUrl()).isEqualTo("http://x");
    assertThat(savedTrue.getDiscovery()).isEqualTo("disc a");
    assertThat(savedTrue.getCreatedAt()).isNotNull();
    assertThat(savedTrue.getUpdatedAt()).isNotNull();

    // When status null -> should default to false per implementation
    BannerDTO dtoNull = new BannerDTO("Title B", null, "http://y", "disc b");
    Banner savedNull = bannerService.create(dtoNull);
    assertThat(savedNull.getStatus()).isFalse();
  }

  @Test
  @DisplayName("update returns null when banner not found")
  void update_notFound_returnsNull() {
    UUID id = UUID.randomUUID();
    when(bannerRepository.findById(id)).thenReturn(Optional.empty());

    Banner updated = bannerService.update(id, new BannerDTO("T", true, "u", "d"));
    assertThat(updated).isNull();
    verify(bannerRepository).findById(id);
    verify(bannerRepository, never()).save(any(Banner.class));
  }

  @Test
  @DisplayName("update changes only non-null fields and updates timestamp")
  void update_partialFields() {
    UUID id = UUID.randomUUID();
    Banner existing = sampleBanner(id, "Old", false);
    LocalDateTime before = existing.getUpdatedAt();
    when(bannerRepository.findById(id)).thenReturn(Optional.of(existing));
    when(bannerRepository.save(any(Banner.class))).thenAnswer(inv -> inv.getArgument(0));

    BannerDTO dto = new BannerDTO(null, true, null, "New disc");
    Banner updated = bannerService.update(id, dto);

    assertThat(updated.getTitle()).isEqualTo("Old");
    assertThat(updated.getStatus()).isTrue();
    assertThat(updated.getImageUrl()).isEqualTo("http://img");
    assertThat(updated.getDiscovery()).isEqualTo("New disc");
    assertThat(updated.getUpdatedAt()).isAfter(before);
    verify(bannerRepository).save(existing);
  }

  @Test
  @DisplayName("delete returns false when not found, true when deleted")
  void delete_paths() {
    UUID missing = UUID.randomUUID();
    when(bannerRepository.findById(missing)).thenReturn(Optional.empty());
    boolean deleted = bannerService.delete(missing);
    assertThat(deleted).isFalse();
    verify(bannerRepository).findById(missing);
    verify(bannerRepository, never()).delete(any(Banner.class));

    UUID found = UUID.randomUUID();
    Banner existing = sampleBanner(found, "T1", true);
    when(bannerRepository.findById(found)).thenReturn(Optional.of(existing));
    boolean deleted2 = bannerService.delete(found);
    assertThat(deleted2).isTrue();
    verify(bannerRepository).delete(existing);
  }

}
