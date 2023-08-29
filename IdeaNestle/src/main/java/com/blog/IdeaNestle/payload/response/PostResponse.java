package com.blog.IdeaNestle.payload.response;

import com.blog.IdeaNestle.model.Category;
import com.blog.IdeaNestle.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String title;
    private String content;
    private String username;
    private List<Comment> comments;
    private List<String> contributions;
    private List<String> categories;

    public PostResponse(String id, String title, String content, String username, List<Comment> comments, List<String> categories) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.comments = comments;
        this.categories = categories;
    }

    public PostResponse(String id, String title, String content, String username, List<Comment> comments) {
    }
}
