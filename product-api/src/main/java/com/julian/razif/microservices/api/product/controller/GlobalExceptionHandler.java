package com.julian.razif.microservices.api.product.controller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult().getAllErrors().stream()
      .map(DefaultMessageSourceResolvable::getDefaultMessage)
      .collect(Collectors.toList());
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("errors", errors);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAnyException(Exception ex) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("errors", List.of("internal server error"));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

}
