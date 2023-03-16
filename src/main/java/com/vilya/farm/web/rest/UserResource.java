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

import javax.validation.Valid;

import java.util.List;

import static com.vilya.farm.constant.ApiConstant.*;

@RestController
@RequestMapping(USER_V1)
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;
  private final AuthenticationService authenticationService;

  @PostMapping(REGISTRATION)
  public RegistrationResponse register(@Valid @RequestBody RegistrationRequest request) {
    return userService.register(request);
  }

  @PostMapping(SIGN_IN)
  public TokenPayload signIn(@RequestBody SignInRequest request) {
    return authenticationService.authenticate(request);
  }

  @GetMapping
  public List<User> findAll() {
    return userService.findAll();
  }
}
