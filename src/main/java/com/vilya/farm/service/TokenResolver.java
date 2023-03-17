package com.vilya.farm.service;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

// import javax.servlet.http.HttpServletRequest;

public interface TokenResolver {
  //  String resolve(HttpServletRequest request);
  String resolve(ServerHttpRequest request);

  String resolve(ServerWebExchange exchange);
}
