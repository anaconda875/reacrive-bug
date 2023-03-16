package com.vilya.farm.dto.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
  private String username, password;
}
