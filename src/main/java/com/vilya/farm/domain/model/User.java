package com.vilya.farm.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "users")
public class User {

  @Id private String id;

  @Indexed(name = "ui_email", unique = true)
  private String email;

  private String password;

  @Indexed(name = "ui_first_name")
  private String firstName;

  @Indexed(name = "ui_last_name")
  private String lastName;

  //  @CassandraType(type = CassandraType.Name.TEXT)
  //  private Gender gender;

  ;
}
