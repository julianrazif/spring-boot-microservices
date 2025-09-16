package com.julian.razif.microservices.api.product.service;

import com.julian.razif.microservices.api.product.dto.UserDTO;
import com.julian.razif.microservices.service.persistence.product.model.User;
import com.julian.razif.microservices.service.persistence.product.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public boolean emailExists(String email) {
    return userRepository.existsByEmail(email);
  }

  @Transactional
  public User register(UserDTO req) {
    User u = new User();
    u.setId(UUID.randomUUID());
    u.setDisplayName(req.displayName());
    u.setEmail(req.email());
    u.setPassword(passwordEncoder.encode(req.password()));
    String role = (req.role() == null || req.role().isBlank()) ? "customer" : req.role();
    u.setRole(role);
    LocalDateTime now = LocalDateTime.now();
    u.setCreatedAt(now);
    u.setUpdatedAt(now);
    return userRepository.save(u);
  }

}
