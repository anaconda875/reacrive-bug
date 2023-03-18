package com.vilya.farm.web.rest;

import com.vilya.farm.domain.model.User;
import com.vilya.farm.dto.request.RegistrationRequest;
import com.vilya.farm.dto.request.SignInRequest;
import com.vilya.farm.dto.response.RegistrationResponse;
import com.vilya.farm.dto.response.TokenPayload;
import com.vilya.farm.service.AuthenticationService;
import com.vilya.farm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

import static com.vilya.farm.constant.ApiConstant.*;

@RestController
@RequestMapping(USER_V1)
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;
  private final AuthenticationService authenticationService;

  @PostMapping(REGISTRATION)
  public Mono<RegistrationResponse> register(
      @Valid @RequestBody Mono<RegistrationRequest> request) {
    return userService.register(request);
  }

  @PostMapping(SIGN_IN)
  public Mono<TokenPayload> signIn(@RequestBody Mono<SignInRequest> request) {
    return authenticationService.authenticate(request);
  }

  @GetMapping
  public Flux<User> findAll() {
    return userService.findAll();
  }
}
