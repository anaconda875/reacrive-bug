package com.vilya.farm.service.impl;

import com.vilya.farm.dto.request.AuthenticationRequest;
import com.vilya.farm.dto.response.TokenPayload;
import com.vilya.farm.service.AuthenticationService;
import com.vilya.farm.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

  private final ReactiveAuthenticationManager authenticationManager;
  private final TokenManager tokenManager;

  @Override
  public Mono<TokenPayload> authenticate(Mono<? extends AuthenticationRequest> request) {
    return request
        .flatMap(
            r ->
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(r.getUsername(), r.getPassword())))
        .map(Authentication::getPrincipal)
        .map(UserDetails.class::cast)
        .map(tokenManager::createAccessToken);
  }
}
