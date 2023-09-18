package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Contribution;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContributionRepository extends MongoRepository<Contribution, Long> {
}
