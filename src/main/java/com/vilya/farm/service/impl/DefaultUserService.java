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

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public RegistrationResponse register(RegistrationRequest request) {
    String email = request.getEmail();
    if (userRepository.existsByEmail(email)) {
      throw new ConflictException(String.format("%s existed", email));
    }

    User user = new User();
    BeanUtils.copyProperties(request, user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    User saved = userRepository.save(user);

    RegistrationResponse response = new RegistrationResponse();
    BeanUtils.copyProperties(saved, response);

    return response;
  }

  @Override
  public List<User> findAll() {
    return userRepository.findAll();
  }
}
