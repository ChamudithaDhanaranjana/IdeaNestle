package com.blog.IdeaNestle.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "contributions")
public class Contribution {

    @Id
    private String id;
    @NotBlank
    private String content;

    @DBRef
    private User contributor;
    private boolean approved;
    private String postId;

    public void setPost(Post post) {

    }
}
