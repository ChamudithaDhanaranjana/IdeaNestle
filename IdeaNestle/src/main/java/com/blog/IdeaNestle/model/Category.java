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

    public void setPostCount(int size) {
    }

    public enum ECategory {
        CATEGORY_TECHNOLOGY("Technology"),
        CATEGORY_AESTHETIC("Aesthetic"),
        CATEGORY_NEWS("News"),
        CATEGORY_NATURE("Nature"),
        CATEGORY_OTHER("Nature");

        private final String displayName;

        ECategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        }
}
