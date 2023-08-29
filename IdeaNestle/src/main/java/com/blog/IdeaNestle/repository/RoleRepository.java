package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@EnableMongoRepositories
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(Role.ERole name);
}
