package com.vilya.farm.service.impl;

import com.vilya.farm.domain.model.User;
import com.vilya.farm.dto.request.RegistrationRequest;
import com.vilya.farm.dto.response.RegistrationResponse;
import com.vilya.farm.exception.ConflictException;
import com.vilya.farm.repository.UserRepository;
import com.vilya.farm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Mono<RegistrationResponse> register(Mono<RegistrationRequest> request) {
    return request
        .zipWhen(r -> userRepository.existsByEmail(r.getEmail()))
        .flatMap(
            t -> {
              if (t.getT2()) {
                return Mono.error(
                    new ConflictException(String.format("%s existed", t.getT1().getEmail())));
              }

              User user = new User();
              BeanUtils.copyProperties(t.getT1(), user);
              user.setPassword(passwordEncoder.encode(user.getPassword()));
              return userRepository
                  .save(user)
                  .map(
                      u -> {
                        RegistrationResponse response = new RegistrationResponse();
                        BeanUtils.copyProperties(u, response);

                        return response;
                      });
            });
  }

  @Override
  public Flux<User> findAll() {
    return userRepository.findAll();
  }
}
