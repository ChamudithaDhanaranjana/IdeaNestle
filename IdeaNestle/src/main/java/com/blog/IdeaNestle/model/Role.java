package com.blog.IdeaNestle.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "roles")
public class Role {
  @Id
  private String id;
  @NotBlank
  private ERole name;

  public enum ERole {
    ROLE_USER,
    ROLE_ADMIN
  }
}
