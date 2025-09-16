package com.julian.razif.microservices.api.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
    List<String> errors = new ArrayList<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      if (fe.getDefaultMessage() != null && !fe.getDefaultMessage().isBlank()) {
        errors.add(fe.getDefaultMessage());
      }
    }
    Map<String, Object> body = new HashMap<>();
    body.put("errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAnyException(Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("errors", List.of("internal server error"));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

}
