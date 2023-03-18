package com.vilya.farm.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.vilya.farm.constant.ApiConstant.*;

@Configuration
@EnableWebFluxSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class JWTSecurityConfig {

  private final ServerAuthenticationConverter authenticationConverter;
  private final ServerAuthenticationEntryPoint entryPoint;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ReactiveAuthenticationManager authenticationManager(
      ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
        new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authenticationManager.setPasswordEncoder(passwordEncoder);

    return authenticationManager;
  }

  @Bean
  public SecurityWebFilterChain filterChain(
      ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager) {
    return http.cors()
        .and()
        .csrf()
        .disable()
        .httpBasic()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(entryPoint)
        .and()
        .authorizeExchange()
        .pathMatchers(USER_V1 + REGISTRATION, USER_V1 + SIGN_IN, "/farm/**", "/v3/**")
        .permitAll()
        .anyExchange()
        .authenticated()
        .and()
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .addFilterAt(
            bearerAuthenticationFilter(authenticationManager),
            SecurityWebFiltersOrder.AUTHENTICATION)
        .build();
  }

  private WebFilter bearerAuthenticationFilter(
      ReactiveAuthenticationManager authenticationManager) {
    AuthenticationWebFilter bearerAuthenticationFilter;
    bearerAuthenticationFilter =
        new AuthenticationWebFilter(authenticationManager) {
          @Override
          public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            return authenticationConverter
                .convert(exchange)
                .switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
                .flatMap(
                    auth ->
                        chain
                            .filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)));
          }
        };
    bearerAuthenticationFilter.setServerAuthenticationConverter(authenticationConverter);
    // bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/api/**"));

    return bearerAuthenticationFilter;
  }
}
