package com.vilya.farm.service.impl;

import com.vilya.farm.exception.InvalidBearerTokenException;
import com.vilya.farm.service.BearerTokenResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DefaultBearerTokenResolver implements BearerTokenResolver {

  private static final Pattern AUTHORIZATION_PATTERN =
      Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

  @Override
  public String resolve(HttpServletRequest request) {
    return resolveFrom(request.getHeader(HttpHeaders.AUTHORIZATION));
  }

  @Override
  public String resolve(ServerHttpRequest request) {
    List<String> values = Optional.ofNullable(request.getHeaders().get(HttpHeaders.AUTHORIZATION)).orElseGet(List::of);
    return resolveFrom(values.size() > 0 ? values.get(0) : null);
  }

  private String resolveFrom(String authorization) {
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
