package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@EnableMongoRepositories
@Repository
public interface UserRepository extends MongoRepository<User,Long> {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<Object> findByUsername(String username);



}
