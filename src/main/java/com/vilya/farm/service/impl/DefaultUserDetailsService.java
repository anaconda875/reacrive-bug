package com.vilya.farm.service.impl;

import com.vilya.farm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements ReactiveUserDetailsService {

  private final UserRepository userRepository;

  @Override
  public Mono<UserDetails> findByUsername(String username) {
    return userRepository
        .findByEmail(username)
        .map(u -> new User(username, u.getPassword(), List.of()))
        .map(UserDetails.class::cast)
        .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)));
  }
}

/*
if(username.equals("user")) {
  return new User(username, "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", Collections.singletonList(new SimpleGrantedAuthority("user")));
  } else if(username.equals("admin")) {
  return new User(username, "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", Collections.singletonList(new SimpleGrantedAuthority("admin")));
  }

  throw new UsernameNotFoundException(username);*/
