package com.blog.IdeaNestle.repository;

import com.blog.IdeaNestle.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, Long> {
    Optional<Object> findById(String id);
    boolean existsById(String id);
    void deleteById(String id);

    List<Post> findByUsernameContainingIgnoreCaseAndTitleContainingIgnoreCaseAndCategoriesContainingIgnoreCase(String searchTerm, String searchTerm1);

    Page<Post> findAllByStatus(Post.PostStatus postStatus, Pageable pageable);

    List<Post> findByUsername(String username);

    int countByUsername(String username);


}
