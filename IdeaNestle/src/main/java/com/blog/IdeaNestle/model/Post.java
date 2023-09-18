package com.blog.IdeaNestle.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post implements Serializable {
    @Id
    private String id;
    @NotBlank
    private PostStatus status = PostStatus.APPROVED;
    @NotBlank
    @Size(max = 50)
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    @Size(max = 30)
    private String username;
    LocalDate localDate = LocalDate.now();

    private List<Category.ECategory> categories = new ArrayList<>();
    @DBRef
    private Set<User> users = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();


    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    private List<Contribution> contributions;

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    public void setCategories(List<Category.ECategory> categories) {
        this.categories = categories;
    }


    public enum PostStatus {
        APPROVED, RESTRICTED
    }
}


