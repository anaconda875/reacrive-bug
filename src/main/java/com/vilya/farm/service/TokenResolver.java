package com.vilya.farm.service;

import org.springframework.http.server.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;

public interface TokenResolver {
  String resolve(HttpServletRequest request);
  String resolve(ServerHttpRequest request);
}
