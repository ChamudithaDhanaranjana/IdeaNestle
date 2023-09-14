package com.blog.IdeaNestle.service.Post;

import com.blog.IdeaNestle.model.Contribution;
import com.blog.IdeaNestle.model.Post;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.ContributionRequest;
import com.blog.IdeaNestle.repository.ContributionRepository;
import com.blog.IdeaNestle.repository.PostRepository;
import com.blog.IdeaNestle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Service
public class ContributionService {
    private final ContributionRepository contributionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Autowired
    public ContributionService(ContributionRepository contributionRepository, PostRepository postRepository, UserRepository userRepository) {
        this.contributionRepository = contributionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }


    public Contribution approveContribution(String contributionId) {
        Contribution contribution = contributionRepository.findById(Long.valueOf(contributionId))
                .orElseThrow(() -> new RuntimeException("Contribution not found"));
        contribution.setApproved(true);
        return contributionRepository.save(contribution);
    }
    public Contribution createContribution(ContributionRequest contributionRequest, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User currentUser;

            if (authentication.getPrincipal() instanceof User) {
                currentUser = (User) authentication.getPrincipal();
            } else {
                String username = authentication.getName();
                Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));

                if (userOptional.isPresent()) {
                    currentUser = userOptional.get();
                } else {
                    // Handle the case where the user is not found
                    throw new IllegalArgumentException("User not found");
                }
            }

            Contribution contribution = new Contribution();
            contribution.setContent(contributionRequest.getContent());
            contribution.setContributor(currentUser);
            contribution.setPostId(contributionRequest.getPostId());

            // Optionally, you can set the Post entity based on the postId
            // Example: contribution.setPost(postRepository.findById(contributionRequest.getPostId()).orElse(null));

            contribution.setApproved(false);
            return contributionRepository.save(contribution);
        } else {
            throw new IllegalArgumentException("User not authenticated");
        }
    }



    public int getContributionCountByUserId(String userId) {
        // Implement the logic to fetch the contribution count from the repository
        // Make sure your repository method is correctly configured
        return contributionRepository.countByContributorId(userId);
    }

    public Long getContributionCount() {
        return contributionRepository.count();
    }
}
