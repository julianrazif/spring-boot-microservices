package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.ProductDTO;
import com.julian.razif.microservices.service.persistence.product.model.Category;
import com.julian.razif.microservices.service.persistence.product.model.Product;
import com.julian.razif.microservices.service.persistence.product.repository.CategoryRepository;
import com.julian.razif.microservices.service.persistence.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  ProductRepository productRepository;
  @Mock
  CategoryRepository categoryRepository;

  @InjectMocks
  ProductService productService;

  private Product sampleProduct(UUID id, String name) {
    Product p = new Product();
    p.setId(id);
    p.setName(name);
    p.setImageUrl("http://img");
    p.setPrice(new BigDecimal("100"));
    p.setStock(new BigDecimal("5"));
    p.setCreatedAt(LocalDateTime.now().minusDays(2));
    p.setUpdatedAt(LocalDateTime.now().minusHours(4));
    Category c = new Category();
    c.setId(UUID.randomUUID());
    c.setName("Cat");
    c.setCreatedAt(LocalDateTime.now().minusDays(10));
    c.setUpdatedAt(LocalDateTime.now().minusDays(5));
    p.setCategory(c);
    return p;
  }

  @Test
  @DisplayName("list applies all filters and delegates to repository")
  void list_withAllFilters() {
    Pageable pageable = PageRequest.of(0, 20);
    Product prod = sampleProduct(UUID.randomUUID(), "iPhone");
    Page<Product> page = new PageImpl<>(List.of(prod), pageable, 1);
    when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

    Page<Product> result = productService.list("iPh", prod.getCategory().getId(), new BigDecimal("10"), new BigDecimal("200"), pageable);

    assertThat(result.getContent()).containsExactly(prod);
    verify(productRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  @DisplayName("getById delegates to repository")
  void getById_delegates() {
    UUID id = UUID.randomUUID();
    Product prod = sampleProduct(id, "TV");
    when(productRepository.findById(id)).thenReturn(Optional.of(prod));
    Optional<Product> found = productService.getById(id);
    assertThat(found).containsSame(prod);
    verify(productRepository).findById(id);
  }

  @Test
  @DisplayName("create returns null when category not found")
  void create_categoryMissing_returnsNull() {
    when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
    ProductDTO req = new ProductDTO(UUID.randomUUID().toString(), " Name ", " http://img ", "100", "2");
    Product created = productService.create(req, UUID.randomUUID(), new BigDecimal("100"), new BigDecimal("2"));
    assertThat(created).isNull();
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("create trims strings, sets ids and timestamps, and saves")
  void create_success() {
    UUID categoryId = UUID.randomUUID();
    Category c = new Category();
    c.setId(categoryId);
    c.setName("Electronics");
    c.setCreatedAt(LocalDateTime.now());
    c.setUpdatedAt(LocalDateTime.now());
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(c));
    when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

    ProductDTO req = new ProductDTO(categoryId.toString(), "  Nice TV  ", "  http://img  ", "1999", "10");
    Product saved = productService.create(req, categoryId, new BigDecimal("1999"), new BigDecimal("10"));

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getName()).isEqualTo("Nice TV");
    assertThat(saved.getImageUrl()).isEqualTo("http://img");
    assertThat(saved.getPrice()).isEqualByComparingTo("1999");
    assertThat(saved.getStock()).isEqualByComparingTo("10");
    assertThat(saved.getCategory()).isSameAs(c);
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());
  }

  @Test
  @DisplayName("update returns null when product not found")
  void update_productMissing_returnsNull() {
    UUID pid = UUID.randomUUID();
    when(productRepository.findById(pid)).thenReturn(Optional.empty());
    Product updated = productService.update(pid, new ProductDTO("c", "n", "u", "1", "1"), UUID.randomUUID(), new BigDecimal("1"), new BigDecimal("1"));
    assertThat(updated).isNull();
    verify(productRepository).findById(pid);
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("update returns null when new category not found")
  void update_categoryMissing_returnsNull() {
    UUID pid = UUID.randomUUID();
    Product existing = sampleProduct(pid, "Old");
    when(productRepository.findById(pid)).thenReturn(Optional.of(existing));
    when(categoryRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    Product updated = productService.update(pid, new ProductDTO("c", "n", "u", "1", "1"), UUID.randomUUID(), new BigDecimal("1"), new BigDecimal("1"));
    assertThat(updated).isNull();
    verify(productRepository, never()).save(any());
  }

  @Test
  @DisplayName("update overwrites fields, sets category and updates timestamp")
  void update_success() {
    UUID pid = UUID.randomUUID();
    Product existing = sampleProduct(pid, "Old Name");
    LocalDateTime prevUpdated = existing.getUpdatedAt();
    when(productRepository.findById(pid)).thenReturn(Optional.of(existing));
    UUID categoryId = UUID.randomUUID();
    Category newCat = new Category();
    newCat.setId(categoryId);
    newCat.setName("New Cat");
    newCat.setCreatedAt(LocalDateTime.now());
    newCat.setUpdatedAt(LocalDateTime.now());
    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(newCat));
    when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

    ProductDTO req = new ProductDTO(categoryId.toString(), "  New  ", "  http://new  ", "200", "3");
    Product updated = productService.update(pid, req, categoryId, new BigDecimal("200"), new BigDecimal("3"));

    assertThat(updated.getName()).isEqualTo("New");
    assertThat(updated.getImageUrl()).isEqualTo("http://new");
    assertThat(updated.getPrice()).isEqualByComparingTo("200");
    assertThat(updated.getStock()).isEqualByComparingTo("3");
    assertThat(updated.getCategory()).isSameAs(newCat);
    assertThat(updated.getUpdatedAt()).isAfter(prevUpdated);
    verify(productRepository).save(existing);
  }

  @Test
  @DisplayName("delete returns false when not found, true when deleted")
  void delete_paths() {
    UUID missing = UUID.randomUUID();
    when(productRepository.findById(missing)).thenReturn(Optional.empty());
    boolean deleted = productService.delete(missing);
    assertThat(deleted).isFalse();
    verify(productRepository).findById(missing);
    verify(productRepository, never()).delete(any(Product.class));

    UUID found = UUID.randomUUID();
    Product existing = sampleProduct(found, "Name");
    when(productRepository.findById(found)).thenReturn(Optional.of(existing));
    boolean deleted2 = productService.delete(found);
    assertThat(deleted2).isTrue();
    verify(productRepository).delete(existing);
  }

}
