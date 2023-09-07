package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommentRepository extends MongoRepository<Comment, Long> {
}
