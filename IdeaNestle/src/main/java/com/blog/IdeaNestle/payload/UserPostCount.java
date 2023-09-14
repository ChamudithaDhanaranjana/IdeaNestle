package com.blog.IdeaNestle.payload;

import lombok.*;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPostCount {
    private Long id;
    private String username;
    private String state;
    private int postCount;

    public UserPostCount(String username, Long postCount) {
        this.username = username;
        this.postCount = Math.toIntExact(postCount);
    }
}
