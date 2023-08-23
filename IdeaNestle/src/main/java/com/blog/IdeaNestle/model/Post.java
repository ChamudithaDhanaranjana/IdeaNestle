package com.blog.IdeaNestle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post {
    private PostStatus status = PostStatus.APPROVED;
    private String title;
    private String content;
    private String username;
    @DBRef
    private Set<User> users = new HashSet<>();
    public enum PostStatus {
        APPROVED, RESTRICTED
    }
}


