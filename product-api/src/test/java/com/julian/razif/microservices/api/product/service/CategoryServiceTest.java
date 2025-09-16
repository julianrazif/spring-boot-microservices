package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.CategoryDTO;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CategoryServiceTest {

  @Mock
  CategoryRepository categoryRepository;

  @InjectMocks
  CategoryService categoryService;

  @Captor
  ArgumentCaptor<Category> categoryCaptor;

  private Category sampleCategory(UUID id, String name) {
    Category c = new Category();
    c.setId(id);
    c.setName(name);
    c.setCreatedAt(LocalDateTime.now().minusDays(1));
    c.setUpdatedAt(LocalDateTime.now().minusHours(2));
    return c;
  }

  @BeforeEach
  void setup() {
    // no-op for now
  }

  @Test
  @DisplayName("list applies name filter and delegates to repository")
  void list_withNameFilter() {
    Pageable pageable = PageRequest.of(0, 10);
    Category existing = sampleCategory(UUID.randomUUID(), "Electronics");
    Page<Category> page = new PageImpl<>(List.of(existing), pageable, 1);
    when(categoryRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<Category> result = categoryService.list("Elect", pageable);

    assertThat(result.getContent()).containsExactly(existing);
    verify(categoryRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("getById delegates to repository")
  void getById_delegates() {
    UUID id = UUID.randomUUID();
    Category existing = sampleCategory(id, "Books");
    when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

    Optional<Category> found = categoryService.getById(id);
    assertThat(found).containsSame(existing);
    verify(categoryRepository).findById(id);
  }

  @Test
  @DisplayName("create sets id, timestamps and saves category")
  void create_setsFieldsAndSaves() {
    when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

    CategoryDTO req = new CategoryDTO("Home Appliances");
    Category saved = categoryService.create(req);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Home Appliances");
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());

    verify(categoryRepository).save(categoryCaptor.capture());
    Category captured = categoryCaptor.getValue();
    assertThat(captured.getId()).isEqualTo(saved.getId());
  }

  @Test
  @DisplayName("update returns null when category not found")
  void update_notFound_returnsNull() {
    UUID id = UUID.randomUUID();
    when(categoryRepository.findById(id)).thenReturn(Optional.empty());

    Category updated = categoryService.update(id, new CategoryDTO("New Name"));
    assertThat(updated).isNull();
    verify(categoryRepository).findById(id);
    verify(categoryRepository, never()).save(any());
  }

  @Test
  @DisplayName("update modifies name when provided and updates timestamp")
  void update_success() {
    UUID id = UUID.randomUUID();
    Category existing = sampleCategory(id, "Old Name");
    LocalDateTime prevUpdatedAt = existing.getUpdatedAt();
    when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
    when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

    Category updated = categoryService.update(id, new CategoryDTO("New Name"));

    assertThat(updated.getName()).isEqualTo("New Name");
    assertThat(updated.getUpdatedAt()).isAfter(prevUpdatedAt);
    verify(categoryRepository).save(existing);
  }

  @Test
  @DisplayName("delete returns false when not found, true when deleted")
  void delete_paths() {
    UUID notFound = UUID.randomUUID();
    when(categoryRepository.findById(notFound)).thenReturn(Optional.empty());

    boolean deleted = categoryService.delete(notFound);
    assertThat(deleted).isFalse();
    verify(categoryRepository).findById(notFound);
    verify(categoryRepository, never()).delete(any(Category.class));

    UUID foundId = UUID.randomUUID();
    Category existing = sampleCategory(foundId, "Misc");
    when(categoryRepository.findById(foundId)).thenReturn(Optional.of(existing));

    boolean deleted2 = categoryService.delete(foundId);
    assertThat(deleted2).isTrue();
    verify(categoryRepository).delete(existing);
  }

}
