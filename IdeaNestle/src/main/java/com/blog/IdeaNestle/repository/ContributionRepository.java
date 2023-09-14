package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Contribution;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ContributionRepository extends MongoRepository<Contribution, Long> {
    List<Contribution> findByContributorUsername(String username);
    int countByContributorId(String contributorId);
}
