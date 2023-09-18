package com.blog.IdeaNestle.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributionRequest {
    private String content;
    private String postId;
}
