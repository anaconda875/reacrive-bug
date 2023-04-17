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

  private static final Sinks.Many<Long> SINK = Sinks.many().multicast().onBackpressureBuffer();
  private static final Flux<Long> FLUX = SINK.asFlux();
  
  private static CountDownLatch countDownLatch = new CountDownLatch(1);
  
  @GetMapping("/mono")
  public Mono<Object> testMono(@RequestBody Mono<SignInRequest> body) {
    countDownLatch = new CountDownLatch(1);
    Mono<Integer> objectMono = Mono.just(25);
    return objectMono
        .flatMap(i -> body.flatMap(b -> doWork(i)));
  }
  
  private Mono<Object> doWork(Integer i) {
    Mono<Object> mono = Mono.fromCallable(() -> {
      System.out.println(Thread.currentThread().getName());
      if (countDownLatch.await(i, TimeUnit.SECONDS)) {
        return i;
      }
      throw new RuntimeException("time exceeded");
    });
    return mono.subscribeOn(Schedulers.parallel());
  }
  
  @GetMapping("/flux")
  public Flux<?> test() {
    return FLUX.doOnNext(l -> System.out.println(Thread.currentThread().getName()))
        /*.subscribeOn(Schedulers.boundedElastic())*/;
  }
  
  @GetMapping("/emit/{s}")
  public Mono<Void> emit(@PathVariable Long s) {
    return Mono.fromRunnable(() -> {
        try {
          Thread.sleep(s * 1000);
          SINK.tryEmitNext(s);
        } catch (Exception e) {
          e.printStackTrace();
        }
    });
  }
  
  @GetMapping("/complete")
  public Mono<Void> complete() {
    return Mono.fromRunnable(SINK::tryEmitComplete);
  }
  
  @GetMapping("/mono/2")
  public Mono<Object> testMono2(Integer a) {
    countDownLatch = new CountDownLatch(1);
    Mono<Integer> mono = Mono.just(99);
    Mono<Integer> objectMono = Mono.just(a);
    return objectMono
        .flatMap(i -> mono.flatMap(b -> doWork(i)));
  }
  
  @GetMapping("/count-down")
  public Mono<Void> countDown() {
    countDownLatch.countDown();
    return Mono.empty();
  }
}
