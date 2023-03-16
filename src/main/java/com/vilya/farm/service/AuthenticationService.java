package com.vilya.farm.service;

import com.vilya.farm.dto.request.AuthenticationRequest;
import com.vilya.farm.dto.response.TokenPayload;

public interface AuthenticationService {

  TokenPayload authenticate(AuthenticationRequest request);
}
