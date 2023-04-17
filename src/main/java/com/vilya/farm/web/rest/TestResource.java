package com.vilya.farm.web.rest;

import com.vilya.farm.service.AbstractAwaitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class TestResource {
  
  private final AbstractAwaitService service;
  
  @GetMapping
  public Mono<Object> test() {
    return service.process("vin", "cid");
  }
  
}
