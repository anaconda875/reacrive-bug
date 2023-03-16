package com.vilya.farm.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidBearerTokenException extends AuthenticationException {

  public InvalidBearerTokenException(String description) {
    super(description);
  }

  public InvalidBearerTokenException(String description, Throwable cause) {
    super(description, cause);
  }
}
