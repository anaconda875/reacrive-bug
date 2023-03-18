package com.vilya.farm.dto.request;

import lombok.Data;

import jakarta.validation.constraints.Pattern;

@Data
public class RegistrationRequest {

  @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
  private String email;

  @Pattern(
      regexp =
          "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.()^\\-+/\\\\_=`<>';:\"{\\[\\]}|~])[A-Za-z\\d@$!%*?&.()^\\-+/\\\\_=`<>';:\"{\\[\\]}|~]{9,}$")
  private String password;

  private String firstName;
  private String lastName;
  //  private Gender gender = Gender.MALE;
}
