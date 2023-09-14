package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@EnableMongoRepositories
@Repository
public interface CategoryRepository extends MongoRepository<Category, Long> {

    Optional<Object> findByName(Category.ECategory name);
}
