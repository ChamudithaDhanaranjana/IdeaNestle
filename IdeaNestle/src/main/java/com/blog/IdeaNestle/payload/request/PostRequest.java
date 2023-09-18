package com.blog.IdeaNestle.payload.request;

import com.blog.IdeaNestle.model.Category;
import com.blog.IdeaNestle.model.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private String username;
    @NotBlank
    private List<Category.ECategory> categories;

}
