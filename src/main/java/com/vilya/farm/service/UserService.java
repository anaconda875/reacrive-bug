package com.vilya.farm.service;

import com.vilya.farm.domain.model.User;
import com.vilya.farm.dto.request.RegistrationRequest;
import com.vilya.farm.dto.response.RegistrationResponse;

import java.util.List;

public interface UserService {

  RegistrationResponse register(RegistrationRequest request);

  List<User> findAll();
}
