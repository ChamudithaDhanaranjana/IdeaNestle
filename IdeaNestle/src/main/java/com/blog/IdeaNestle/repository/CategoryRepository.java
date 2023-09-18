package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, Long> {

    Optional<Object> findByName(Category.ECategory name);
}
