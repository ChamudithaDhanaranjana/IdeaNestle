package com.blog.IdeaNestle.service;

import com.blog.IdeaNestle.model.Comment;
import com.blog.IdeaNestle.model.Contribution;
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
    @Autowired
    private PostRepository postRepository;

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


    public PostResponse getPostById(String id) {
        Post post = (Post) postRepository.findById(id)
                .orElseThrow(() -> new CustomException("Post not found with id: " + id));

        Optional<Object> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            throw new CustomException("Post not found with id: " + id);
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

    public boolean deletePostById(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id)
            ;
            return true;
        }
        return false;
    }

    private PostResponse mapToResponse(Post post) {
        PostResponse postResponse = new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUsername(),
                post.getComments(),
                post.getContributions()// Use the username field from the Post entity
        );
        return postResponse;
    }


    private Post mapToEntity(PostRequest request) {
        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        return post;
    }

    public boolean restrictPost(String postId) {
        Optional<Object> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = (Post) optionalPost.get();
            post.setStatus(Post.PostStatus.RESTRICTED);
            postRepository.save(post);
            return true;
        } else {
            return false;
        }
    }

    public Contribution addContribution(String postId, String content, String contributorUsername) {
        Optional<Object> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = (Post) optionalPost.get();

            Contribution contribution = new Contribution();
            contribution.setId();
            contribution.setContent(content);
            contribution.setApproved(false);
            contribution.setUsername(contributorUsername);

            post.getContributions().add(contribution);
            postRepository.save(post);

            return contribution;
        } else {
            return null;
        }
    }

    public boolean approveContribution(String postId, String contributionId, String contributorUsername) {
        Optional<Object> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = (Post) optionalPost.get();

            Optional<Contribution> optionalContribution = post.getContributions().stream()
                    .filter(c -> c.getId().equals(contributionId) && c.getUsername().equals(contributorUsername))
                    .findFirst();

            if (optionalContribution.isPresent()) {
                Contribution contribution = optionalContribution.get();
                contribution.setApproved(true);
                postRepository.save(post);
                return true;
            }
        }
        return false;
    }


    public boolean addCommentToPostById(String id, Comment comment) {
        Optional<Object> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = (Post) optionalPost.get();
            post.addComment(comment);
            postRepository.save(post);
            return true;
        } else {
            return false;
        }
    }


}
