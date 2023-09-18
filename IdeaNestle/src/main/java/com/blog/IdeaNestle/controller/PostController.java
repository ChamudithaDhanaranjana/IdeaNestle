package com.blog.IdeaNestle.controller;


import com.blog.IdeaNestle.model.Comment;
import com.blog.IdeaNestle.payload.request.PostRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.service.Post.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;
    @Autowired
    private AuthenticationManager authenticationManager;
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
    public ResponseEntity<List<PostResponse>> getAllApprovedPosts() {
        List<PostResponse> approvedPosts = postService.getAllApprovedPosts();
        return ResponseEntity.ok(approvedPosts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String id) {
        PostResponse postResponse = postService.getPostById(id);
        if (postResponse != null) {
            return ResponseEntity.ok(postResponse);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @DeleteMapping("/delete/post/{id}")
    public ResponseEntity<String> deletePostById(@PathVariable String id) {
        boolean deleted = postService.deletePostById(id);

        if (deleted) {
            return ResponseEntity.ok("Post deleted successfully"); // Return 200 OK with a success message
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if student not found
        }
    }

    @PostMapping("/{postId}/restrict")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> restrictPost(@PathVariable String postId) {
        boolean isPostRestricted = postService.restrictPost(postId);
        if (isPostRestricted) {
            return ResponseEntity.ok("Post has been restricted");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/add-comment")
    public ResponseEntity<String> addCommentToPostById(@PathVariable String id, @RequestBody Comment comment) {
        boolean isCommentAdded = postService.addCommentToPostById(id, comment);
        if (isCommentAdded) {
            return ResponseEntity.ok("Comment added to post");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
