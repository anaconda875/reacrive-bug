package com.vilya.farm.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// @RestControllerAdvice
public class RestExceptionHandler {

  //  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Void> handle1(RuntimeException e) {
    throw new NullPointerException("NULL");
    //    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  }

  //  @ExceptionHandler(NullPointerException.class)
  public ResponseEntity<Void> handle2(NullPointerException e) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }
}
