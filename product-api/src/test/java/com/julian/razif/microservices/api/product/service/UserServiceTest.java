package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.UserDTO;
import com.julian.razif.microservices.service.persistence.product.model.User;
import com.julian.razif.microservices.service.persistence.product.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  UserRepository userRepository;
  @Mock
  PasswordEncoder passwordEncoder;

  @InjectMocks
  UserService userService;

  @Test
  @DisplayName("emailExists delegates to repository and returns true")
  void emailExists_true() {
    when(userRepository.existsByEmail("a@b.com")).thenReturn(true);

    boolean exists = userService.emailExists("a@b.com");

    assertThat(exists).isTrue();
    verify(userRepository).existsByEmail(eq("a@b.com"));
  }

  @Test
  @DisplayName("emailExists delegates to repository and returns false")
  void emailExists_false() {
    when(userRepository.existsByEmail("x@y.com")).thenReturn(false);

    boolean exists = userService.emailExists("x@y.com");

    assertThat(exists).isFalse();
    verify(userRepository).existsByEmail(eq("x@y.com"));
  }

  @Test
  @DisplayName("register encodes password, keeps provided role, sets ids and timestamps, and saves")
  void register_withProvidedRole() {
    when(passwordEncoder.encode("secret")).thenReturn("{enc}secret");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    LocalDateTime before = LocalDateTime.now().minusSeconds(1);
    UserDTO req = new UserDTO("Alice", "alice@gmail.com", "secret", "admin");

    User saved = userService.register(req);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getDisplayName()).isEqualTo("Alice");
    assertThat(saved.getEmail()).isEqualTo("alice@gmail.com");
    assertThat(saved.getPassword()).isEqualTo("{enc}secret");
    assertThat(saved.getRole()).isEqualTo("admin");
    assertThat(saved.getCreatedAt()).isNotNull();
    assertThat(saved.getUpdatedAt()).isNotNull();
    assertThat(Duration.between(before, saved.getCreatedAt()).getSeconds()).isLessThan(5);
    assertThat(saved.getCreatedAt()).isEqualTo(saved.getUpdatedAt());

    // ensure encoder called
    verify(passwordEncoder).encode("secret");
    // ensure repository.save called with the same object we verified
    ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(captor.capture());
    assertThat(captor.getValue()).isSameAs(saved);
  }

  @Test
  @DisplayName("register defaults role to 'customer' when role is null or blank")
  void register_defaultRole_whenNullOrBlank() {
    when(passwordEncoder.encode("1234")).thenReturn("{enc}1234");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    // null role
    UserDTO reqNull = new UserDTO("Bob Marley", "bob@gmail.com", "1234", null);
    User savedNull = userService.register(reqNull);
    assertThat(savedNull.getRole()).isEqualTo("customer");

    // blank role
    UserDTO reqBlank = new UserDTO("Bob Marley", "bob2@gmail.com", "1234", "   ");
    User savedBlank = userService.register(reqBlank);
    assertThat(savedBlank.getRole()).isEqualTo("customer");

    verify(passwordEncoder, times(2)).encode("1234");
    verify(userRepository, times(2)).save(any(User.class));
  }

}
