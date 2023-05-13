package com.vilya.farm.web.rest;

import com.vilya.farm.domain.model.User;
import com.vilya.farm.dto.request.RegistrationRequest;
import com.vilya.farm.dto.request.SignInRequest;
import com.vilya.farm.dto.response.RegistrationResponse;
import com.vilya.farm.dto.response.TokenPayload;
import com.vilya.farm.service.AuthenticationService;
import com.vilya.farm.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
//import javax.validation.Valid;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
  @Operation(security = {@SecurityRequirement(name = "bearer-key")})
  public Flux<User> findAll() {
    return userService.findAll();
  }

  private static CountDownLatch countDownLatch = new CountDownLatch(1);
  
  @GetMapping("/mono")
  public Mono<Object> testMono(@RequestBody Mono<SignInRequest> body) {
    countDownLatch = new CountDownLatch(1);
    Mono<Integer> objectMono = Mono.just(25);
    return objectMono
        .flatMap(i -> body.flatMap(b -> doWork(i)));
  }
  
  private Mono<Object> doWork(Integer i) {
    return Mono.fromCallable(() -> {
      System.out.println(Thread.currentThread().getName());
      if (countDownLatch.await(i, TimeUnit.SECONDS)) {
        return i;
      }
      throw new RuntimeException("time exceeded");
    });
  }
  
  @GetMapping("/mono/2")
  public Mono<Object> testMono2(Integer a) {
    countDownLatch = new CountDownLatch(1);
    Mono<Integer> mono = Mono.just(99);
    Mono<Integer> objectMono = Mono.just(a);
    return objectMono
        .flatMap(i -> mono.flatMap(b -> doWork(i)));
  }
  
}
