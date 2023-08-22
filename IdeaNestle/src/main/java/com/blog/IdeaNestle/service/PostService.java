package com.blog.IdeaNestle.service;

import com.blog.IdeaNestle.model.Post;
import com.blog.IdeaNestle.payload.request.PostRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.repository.PostRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostResponse> getAllPosts() {
        List<Post> post = postRepository.findAll();
        if(post.isEmpty()) {
            return new ArrayList<>();
        }

        return post.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PostResponse getPostByTitle(String title) {
        Post post = (Post) postRepository.findByTitle(title)
                .orElseThrow(() -> new CustomException("Post not found with title: " + title));

        Optional<Object> postOptional = postRepository.findByTitle(title);
        if (postOptional.isEmpty()) {
            throw new CustomException("Post not found with title: " + title);
        }
        return mapToResponse(post);
    }

    public PostResponse createPost(PostRequest postRequest) {
//        Post post = mapToEntity(postRequest);
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        postRepository.save(post);
        return null;
    }

    public boolean deletePostByTitle(String title) {
        if (postRepository.existsByTitle(title)) {
            postRepository.deleteByTitle(title)
            ;
            return true;
        }
        return false;
    }

    private @NotNull PostResponse mapToResponse(@NotNull Post post) {
        return new PostResponse(
                post.getTitle(),
                post.getContent()
        );
    }

    private Post mapToEntity(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return post;
    }


}
