package com.vilya.farm.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SignInRequest extends AuthenticationRequest {

  private String email;

  @Override
  @JsonIgnore
  public void setUsername(String username) {
    super.setUsername(username);
  }

  @Override
  @JsonIgnore
  public String getUsername() {
    return super.getUsername();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    setUsername(email);
  }
}
