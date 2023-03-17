package com.vilya.farm.service;

import com.vilya.farm.domain.model.User;
import com.vilya.farm.dto.request.RegistrationRequest;
import com.vilya.farm.dto.response.RegistrationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Mono<RegistrationResponse> register(Mono<RegistrationRequest> request);

  Flux<User> findAll();
}
