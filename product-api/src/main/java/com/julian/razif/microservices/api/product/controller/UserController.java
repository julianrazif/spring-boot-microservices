package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.LoginDTO;
import com.julian.razif.microservices.api.product.dto.UserDTO;
import com.julian.razif.microservices.service.persistence.product.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

  @PersistenceContext(unitName = "product-pu")
  private EntityManager em;

  private final PasswordEncoder passwordEncoder;

  public UserController(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody(required = false) LoginDTO req) {
    Map<String, Object> body = new LinkedHashMap<>();
    // In this Basic Auth setup, clients should authenticate via the Authorization header.
    // We return a placeholder token field to be compatible with the documented contract.
    body.put("access_token", "basic");
    return ResponseEntity.ok(body);
  }

  @PostMapping("/register")
  @Transactional
  public ResponseEntity<?> register(@Valid @RequestBody UserDTO req) {
    // Check email uniqueness
    Long count = em.createQuery("select count(u) from User u where u.email = :email", Long.class)
      .setParameter("email", req.email())
      .getSingleResult();
    if (count != null && count > 0) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", java.util.List.of("email already has taken"));
      return ResponseEntity.badRequest().body(body);
    }

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

    em.persist(u);

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("message", "register success");
    data.put("displayName", u.getDisplayName());
    data.put("email", u.getEmail());
    data.put("role", u.getRole());

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("data", data);
    return new ResponseEntity<>(body, HttpStatus.CREATED);
  }

}
