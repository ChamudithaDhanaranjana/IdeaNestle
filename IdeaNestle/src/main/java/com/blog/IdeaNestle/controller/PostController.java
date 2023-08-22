package com.blog.IdeaNestle.controller;


import com.blog.IdeaNestle.payload.request.PostRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/posts")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    @Autowired
    private PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createPost(@RequestBody PostRequest postRequest) {
        PostResponse createdPost = postService.createPost(postRequest);
        logger.info("Post successfully created");
        return ResponseEntity.ok("Post created successfully");
    }

    @GetMapping("/get/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{title}")
    public ResponseEntity<String> getPostByTitle(@PathVariable String title) {
        logger.info("Fetching post with title: {}", title);

        String post = String.valueOf(postService.getPostByTitle(title));
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @DeleteMapping("/delete/post/{title}") // Define the endpoint mapping with a path variable
    public ResponseEntity<String> deletePostByTitle(@PathVariable String title) {
        boolean deleted = postService.deletePostByTitle(title);

        if (deleted) {
            return ResponseEntity.ok("Post deleted successfully"); // Return 200 OK with a success message
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if student not found
        }
    }
}
