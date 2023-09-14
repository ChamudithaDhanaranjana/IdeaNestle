package com.blog.IdeaNestle.controller;

import com.blog.IdeaNestle.model.Contribution;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.ContributionRequest;
import com.blog.IdeaNestle.service.Post.ContributionService;
import com.blog.IdeaNestle.service.Post.PostService;
import com.blog.IdeaNestle.service.User.AuthService;
import com.blog.IdeaNestle.service.User.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/contributions")
public class ContributionController {
    private static final Logger logger = LoggerFactory.getLogger(ContributionController.class);
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createContribution(@RequestBody ContributionRequest contributionRequest) {
        Contribution createdContribution = contributionService.createContribution(contributionRequest, SecurityContextHolder.getContext().getAuthentication());
        logger.info("Contribution successfully created");
        return ResponseEntity.ok("Contribution created successfully");
    }


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/approve/{contributionId}")
    public ResponseEntity<String> approveContribution(@PathVariable String contributionId) {
        contributionService.approveContribution(contributionId);
        return ResponseEntity.ok("Contribution approved successfully");
    }

    @GetMapping("/{userId}/count")
    public Integer getUserContributionCount(@PathVariable String userId, Authentication authentication) {
        // Ensure that the currently authenticated user is requesting their own count
        UserDetailsImpl currentUser = (UserDetailsImpl) authentication.getPrincipal();
        String currentUserId = String.valueOf(currentUser.getId());

        if (!currentUserId.equals(userId)) {
            throw new IllegalArgumentException("Access denied");
        }

        // Fetch and return the contribution count for the user
        return contributionService.getContributionCountByUserId(userId);
    }

    @GetMapping("/count")
    public Long getContributionCount() {
        return contributionService.getContributionCount();
    }

}
