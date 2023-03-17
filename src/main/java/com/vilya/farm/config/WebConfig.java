package com.vilya.farm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {

  @Bean
  public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
    return new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED);
  }
}
