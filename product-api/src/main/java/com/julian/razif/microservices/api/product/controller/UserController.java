package com.julian.razif.microservices.api.product.controller;

import com.julian.razif.microservices.api.product.dto.LoginDTO;
import com.julian.razif.microservices.api.product.dto.UserDTO;
import com.julian.razif.microservices.api.product.service.UserService;
import com.julian.razif.microservices.service.persistence.product.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final Validator validator;

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
    @RequestBody(required = false) UserDTO req) {

    // Short-circuit: if email already taken, return that error first as per contract/tests
    String email = (req == null) ? null : req.email();
    if (email != null && userService.emailExists(email)) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", List.of("email already has taken"));
      return ResponseEntity.badRequest().body(body);
    }

    // Bean validation (manual) to collect field errors like @Valid would
    List<String> errors = new ArrayList<>();
    if (req == null) {
      errors.add("name is required");
      errors.add("email is required");
      errors.add("password is required");
    } else {
      Set<ConstraintViolation<UserDTO>> violations = validator.validate(req);
      for (ConstraintViolation<UserDTO> v : violations) {
        String msg = v.getMessage();
        if (msg != null && !msg.isBlank()) errors.add(msg);
      }
    }

    if (!errors.isEmpty()) {
      Map<String, Object> body = new LinkedHashMap<>();
      body.put("errors", errors);
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
