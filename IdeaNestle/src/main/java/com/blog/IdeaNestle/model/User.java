package com.blog.IdeaNestle.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@Setter
@Getter
@NoArgsConstructor
@Document(collection = "users")
public class User implements Serializable {

  @Transient
  public static String SEQUENCE_NAME = "user_sequence";

  @Id
  private int id;

  private String firstName;
  private String lastName;
  private Gender gender;
  private LocalDate dob;

  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;

  @DBRef
  private Set<Role> roles = new HashSet<>();

  private UserState state;

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }


  public void setUsername(String username) {

  }

  public enum Gender {
    MALE,
    FEMALE,
    OTHER
  }
  public enum UserState {
    ACTIVE,
    INACTIVE;

  }
}



