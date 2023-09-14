package com.blog.IdeaNestle.payload.response;

import com.blog.IdeaNestle.model.Category;
import com.blog.IdeaNestle.model.Comment;
import com.blog.IdeaNestle.model.Post;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private String id;
    private String status;
    private String title;
    private String content;
    private String username;
    private LocalDate localDate;
    private List<Comment> comments;
    private List<String> contributions;
    private List<Category.ECategory> categories;

    public PostResponse(String id, Post.PostStatus status, String title, String content, String username, LocalDate localDate, List<Comment> comments, List<String> contributionContents, List<Category.ECategory> categories) {
        this.id= id;
        this.status= String.valueOf(status);
        this.title= title;
        this.content= content;
        this.username= username;
        this.localDate= localDate;
        this.comments= comments;
        this.contributions= contributionContents;
        this.categories= categories;

    }
}
