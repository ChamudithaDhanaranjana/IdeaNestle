package com.blog.IdeaNestle.controller;

import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.ContributionRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.service.Post.ContributionService;
import com.blog.IdeaNestle.service.Post.PostService;
import com.blog.IdeaNestle.service.User.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contributions")
public class ContributionController {
    private final ContributionService contributionService;
    private final AuthService authService;
    private final PostService postService;

    @Autowired
    public ContributionController(ContributionService contributionService, AuthService authService, PostService postService) {
        this.contributionService = contributionService;
        this.authService = authService;
        this.postService = postService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/create")
    public ResponseEntity<String> createContribution(@RequestBody ContributionRequest contributionRequest,
                                                     @AuthenticationPrincipal User contributor) {
        PostResponse postResponse = postService.getPostById(contributionRequest.getPostId()); // Get the associated post
        contributionService.createContribution(contributionRequest, contributor);
        return ResponseEntity.ok("Contribution created successfully");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/approve/{contributionId}")
    public ResponseEntity<String> approveContribution(@PathVariable String contributionId) {
        contributionService.approveContribution(contributionId);
        return ResponseEntity.ok("Contribution approved successfully");
    }
}
