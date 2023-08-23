package com.blog.IdeaNestle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserUpdateRequest {
    private String username;
    private String password;
    private String email;
}
