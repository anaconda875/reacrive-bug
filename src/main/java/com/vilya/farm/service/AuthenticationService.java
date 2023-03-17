package com.vilya.farm.service;

import com.vilya.farm.dto.request.AuthenticationRequest;
import com.vilya.farm.dto.response.TokenPayload;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

  Mono<TokenPayload> authenticate(Mono<? extends AuthenticationRequest> request);
}
