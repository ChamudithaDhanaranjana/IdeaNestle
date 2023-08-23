package com.blog.IdeaNestle.service;

import com.blog.IdeaNestle.model.Post;
import com.blog.IdeaNestle.model.User;
import com.blog.IdeaNestle.payload.request.PostRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.repository.PostRepository;
import com.blog.IdeaNestle.repository.UserRepository;
import com.blog.IdeaNestle.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostService {
    @Autowired
    private UserRepository userRepository;

    private final PostRepository postRepository;

    private final JwtUtils jwtUtils;
    @Autowired
    public PostService(PostRepository postRepository, JwtUtils jwtUtils) {
        this.postRepository = postRepository;
        this.jwtUtils = jwtUtils;
    }

    public List<PostResponse> getAllApprovedPosts() {
        List<Post> approvedPosts = postRepository.findAllByStatus(Post.PostStatus.APPROVED);
        if (approvedPosts.isEmpty()) {
            return new ArrayList<>();
        }

        return approvedPosts.stream()
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUsername(username);
        post.setStatus(Post.PostStatus.APPROVED); // Set the status to approved

        // Save the post to get its ID
        post = postRepository.save(post);

        User user = userRepository.findByUsername(username); // Fetch the user
        int restrictedPostCount = postRepository.countByUsersAndStatus(user, Post.PostStatus.RESTRICTED);
        if (post.getStatus() == Post.PostStatus.RESTRICTED) {

            if (restrictedPostCount >= 5) {
                user.setState(User.UserState.INACTIVE);
                userRepository.save(user);
            }
        } else {
            // Check the user's state and post count for posts with status RESTRICTED

            if (user.getState() == User.UserState.ACTIVE && restrictedPostCount >= 5) {
                user.setState(User.UserState.INACTIVE);
                userRepository.save(user);
            }
        }

        return mapToResponse(post);
    }




    public boolean deletePostByTitle(String title) {
        if (postRepository.existsByTitle(title)) {
            postRepository.deleteByTitle(title)
            ;
            return true;
        }
        return false;
    }

    private PostResponse mapToResponse(Post post) {
        return new PostResponse(
                post.getTitle(),
                post.getContent(),
                post.getUsername() // Use the username field from the Post entity
        );
    }


    private Post mapToEntity(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return post;
    }


}
