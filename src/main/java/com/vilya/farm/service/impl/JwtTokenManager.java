package com.vilya.farm.service.impl;

import com.vilya.farm.config.JwtSetting;
import com.vilya.farm.dto.response.TokenPayload;
import com.vilya.farm.exception.InvalidBearerTokenException;
import com.vilya.farm.service.TokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtSetting.class)
public class JwtTokenManager implements TokenManager {

  private final JwtSetting jwtSetting;

  @Override
  public Jws<Claims> parseClaims(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(jwtSetting.getKey()).build().parseClaimsJws(token);
    } catch (Exception e) {
      throw new InvalidBearerTokenException(e.getMessage(), e);
      //      return null;
    }
  }

  @Override
  public TokenPayload createAccessToken(UserDetails userDetails) {
    if (userDetails == null) {
      throw new IllegalArgumentException("Cannot create JWT Token without auth user details");
    }

    Map<String, Object> claims = new HashMap<>();
    claims.put(
        "roles",
        userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()));
    long currentTimeMillis = System.currentTimeMillis();
    Date exp = new Date(currentTimeMillis + jwtSetting.getAccessTokenExpiration().toMillis());
    String token =
        Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuer(jwtSetting.getIssuer())
            .setIssuedAt(new Date(currentTimeMillis))
            .setExpiration(exp)
            .signWith(jwtSetting.getKey())
            .compact();

    return new TokenPayload(token, exp.getTime());
  }
}
