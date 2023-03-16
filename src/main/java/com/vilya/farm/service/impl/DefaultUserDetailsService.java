package com.vilya.farm.service.impl;

import com.vilya.farm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .map(u -> new User(username, u.getPassword(), List.of()))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}

/*
if(username.equals("user")) {
  return new User(username, "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", Collections.singletonList(new SimpleGrantedAuthority("user")));
  } else if(username.equals("admin")) {
  return new User(username, "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", Collections.singletonList(new SimpleGrantedAuthority("admin")));
  }

  throw new UsernameNotFoundException(username);*/
