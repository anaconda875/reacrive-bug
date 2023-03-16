package com.vilya.farm.service;

import com.vilya.farm.dto.response.TokenPayload;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenManager {

  Jws<Claims> parseClaims(String token);

  TokenPayload createAccessToken(UserDetails userDetails);
}
