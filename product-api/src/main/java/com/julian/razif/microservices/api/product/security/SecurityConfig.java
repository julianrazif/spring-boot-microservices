package com.julian.razif.microservices.api.product.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
    UserDetails admin = User.withUsername("admin")
      .password(encoder.encode("admin123"))
      .roles("ADMIN")
      .build();

    UserDetails customer = User.withUsername("customer")
      .password(encoder.encode("customer123"))
      .roles("CUSTOMER")
      .build();

    return new MapReactiveUserDetailsService(admin, customer);
  }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // we'll enable explicitly
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .logout(ServerHttpSecurity.LogoutSpec::disable);

    // Enable HTTP Basic
    http.httpBasic(httpBasicSpec -> {
    });

    // Custom error responses for 401 and 403
    http.exceptionHandling(ex -> ex
      .authenticationEntryPoint((exchange, e) -> {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = "{\"errors\":[\"authentication failed\"]}".getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
      })
      .accessDeniedHandler((exchange, e) -> {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = "{\"errors\":[\"access rejected\"]}".getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
      })
    );

    http.authorizeExchange(exchanges -> exchanges
      // Public endpoints
      .pathMatchers(HttpMethod.POST, "/login", "/register").permitAll()

      // Customer cart endpoints require authentication (place before general customer GET rule)
      .pathMatchers("/customer/carts/**").hasAnyRole("CUSTOMER", "ADMIN")

      // Public customer browse endpoints
      .pathMatchers(HttpMethod.GET, "/customer/**").permitAll()

      // Admin-only endpoints
      .pathMatchers("/products/**", "/categories/**", "/banners/**").hasRole("ADMIN")

      // Everything else requires authentication
      .anyExchange().authenticated()
    );

    return http.build();
  }
}
