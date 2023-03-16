package com.vilya.farm.service.impl;

import com.vilya.farm.exception.InvalidBearerTokenException;
import com.vilya.farm.service.BearerTokenResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultBearerTokenResolver implements BearerTokenResolver {

  private static final Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  @Override
  public String resolve(HttpServletRequest request) {
    return resolveFromAuthorizationHeader(request);
  }

  private String resolveFromAuthorizationHeader(HttpServletRequest request) {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (org.apache.commons.lang3.StringUtils.isBlank(authorization)
        || !StringUtils.startsWithIgnoreCase(authorization, "bearer")) {
      return null;
    }
    Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorization);
    if (!matcher.matches()) {
      throw new InvalidBearerTokenException("Bearer token is malformed");
    }
    return matcher.group("token");
  }
}
