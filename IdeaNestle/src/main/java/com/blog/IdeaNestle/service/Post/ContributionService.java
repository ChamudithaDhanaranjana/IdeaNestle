package com.blog.IdeaNestle.service.Post;

import com.blog.IdeaNestle.model.Contribution;
import com.blog.IdeaNestle.model.Post;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.ContributionRequest;
import com.blog.IdeaNestle.repository.ContributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContributionService {
    private final ContributionRepository contributionRepository;

    @Autowired
    public ContributionService(ContributionRepository contributionRepository) {
        this.contributionRepository = contributionRepository;
    }

    public Contribution createContribution(ContributionRequest contributionRequest, User currentUser) {
        Contribution contribution = new Contribution();
        contribution.setContent(contributionRequest.getContent());
        contribution.setContributor(currentUser);
        contribution.setPostId(contributionRequest.getPostId());  // Set the actual Post entity
        contribution.setApproved(false);
        return contributionRepository.save(contribution);
    }

    public Contribution approveContribution(String contributionId) {
        Contribution contribution = contributionRepository.findById(Long.valueOf(contributionId))
                .orElseThrow(() -> new RuntimeException("Contribution not found"));
        contribution.setApproved(true);
        return contributionRepository.save(contribution);
    }
}
