package com.blog.IdeaNestle.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "categories")
public class Category implements Serializable {
    @Id
    private String id;
    @NotBlank
    private ECategory name;

    public enum ECategory {
        CATEGORY_TECHNOLOGY,
        CATEGORY_AESTHETIC,
        CATEGORY_NEWS,
        CATEGORY_NATURE,
        CATEGORY_OTHER
    }
}
