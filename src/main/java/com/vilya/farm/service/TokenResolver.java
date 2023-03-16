package com.vilya.farm.service;

import javax.servlet.http.HttpServletRequest;

public interface TokenResolver {

  String resolve(HttpServletRequest request);
}
