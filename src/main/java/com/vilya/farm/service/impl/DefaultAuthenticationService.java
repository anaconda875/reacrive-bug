package com.vilya.farm.service.impl;

import com.vilya.farm.dto.request.AuthenticationRequest;
import com.vilya.farm.dto.response.TokenPayload;
import com.vilya.farm.service.AuthenticationService;
import com.vilya.farm.service.TokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final TokenManager tokenManager;

  @Override
  public TokenPayload authenticate(AuthenticationRequest request) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    return tokenManager.createAccessToken((UserDetails) authentication.getPrincipal());
  }
}
