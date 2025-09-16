package com.julian.razif.microservices.api.product.controller;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for GlobalExceptionHandler behaviors.
 */
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  // Helper to build a MethodParameter instance for exception constructors
  private static MethodParameter sampleMethodParameter() throws NoSuchMethodException {
    class Dummy {
      @SuppressWarnings("unused")
      void f(String s) {
      }
    }
    Method m = Dummy.class.getDeclaredMethod("f", String.class);
    return new MethodParameter(m, 0);
  }

  @Test
  void handleWebFluxValidationException_collectsNonBlankMessagesAndIgnoresBlank() throws Exception {
    BindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
    br.addError(new FieldError("obj", "name", "name must not be blank"));
    br.addError(new FieldError("obj", "desc", " ")); // should be ignored as blank

    WebExchangeBindException ex = new WebExchangeBindException(sampleMethodParameter(), br);

    ResponseEntity<Map<String, Object>> resp = handler.handleWebFluxValidationException(ex);

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(resp.getBody()).isNotNull();
    assertThat(resp.getBody()).containsKey("errors");
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) resp.getBody().get("errors");
    assertThat(errors).containsExactly("name must not be blank");
  }

  @Test
  void handleValidationException_collectsNonBlankMessagesAndIgnoresBlank() throws Exception {
    BindingResult br = new BeanPropertyBindingResult(new Object(), "obj");
    br.addError(new FieldError("obj", "name", "must not be null"));
    br.addError(new FieldError("obj", "desc", "")); // empty -> ignored

    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(sampleMethodParameter(), br);

    ResponseEntity<Map<String, Object>> resp = handler.handleValidationException(ex);

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(resp.getBody()).isNotNull();
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) resp.getBody().get("errors");
    assertThat(errors).containsExactly("must not be null");
  }

  @Test
  void handleAnyException_returnsInternalServerErrorMessage() {
    Exception ex = new RuntimeException("boom");

    ResponseEntity<Map<String, Object>> resp = handler.handleAnyException(ex);

    assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(resp.getBody()).isNotNull();
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) resp.getBody().get("errors");
    assertThat(errors).containsExactly("internal server error");
  }

}
