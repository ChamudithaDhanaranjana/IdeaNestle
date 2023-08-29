package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Post;
import com.blog.IdeaNestle.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    List<Post> findAllByStatus(Post.PostStatus postStatus);
    Optional<Object> findById(String id);
    boolean existsById(String id);
    void deleteById(String id);

}
