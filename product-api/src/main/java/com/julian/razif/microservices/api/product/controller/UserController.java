package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.LoginDTO;
import com.julian.razif.microservices.api.product.dto.UserDTO;
import com.julian.razif.microservices.api.product.service.UserService;
import com.julian.razif.microservices.service.persistence.product.model.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<?> login(
    @RequestBody(required = false) LoginDTO req) {

    Map<String, Object> body = new LinkedHashMap<>();
    // In this Basic Auth setup, clients should authenticate via the Authorization header.
    // We return a placeholder token field to be compatible with the documented contract.
    body.put("access_token", "basic");
    return ResponseEntity.ok(body);
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(
    @Valid @RequestBody UserDTO req) {

    // Check email uniqueness
    if (userService.emailExists(req.email())) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", java.util.List.of("email already has taken"));
      return ResponseEntity.badRequest().body(body);
    }

    User u = userService.register(req);

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
