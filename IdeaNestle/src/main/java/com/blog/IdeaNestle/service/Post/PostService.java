package com.blog.IdeaNestle.service.Post;

import com.blog.IdeaNestle.controller.AuthController;
import com.blog.IdeaNestle.model.*;
import com.blog.IdeaNestle.payload.request.PostRequest;
import com.blog.IdeaNestle.payload.response.PostResponse;
import com.blog.IdeaNestle.repository.*;
import com.blog.IdeaNestle.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;



@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final ContributionRepository contributionRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    private final JwtUtils jwtUtils;
    private List<String> contributionContents;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public PostService(PostRepository postRepository, JwtUtils jwtUtils, ContributionRepository contributionRepository) {
        this.postRepository = postRepository;
        this.jwtUtils = jwtUtils;
        this.contributionRepository = contributionRepository;
    }

    public Page<PostResponse> getAllApprovedPosts(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 3); // Set the page size to 3
        Page<Post> approvedPosts = postRepository.findAllByStatus(Post.PostStatus.APPROVED, pageable);
        if (approvedPosts.isEmpty()) {
            return null;
        }
        return approvedPosts.map(this::mapToResponse);
    }

    public Page<PostResponse> getAllPosts(int page) {
        Pageable pageable = PageRequest.of(page, 5); // Set the page size to your desired value
        Page<Post> postPage = postRepository.findAll(pageable);

        return postPage.map(this::mapToResponse);
    }


    public PostResponse getPostById(String id) {
        Post post = (Post) postRepository.findById(id).orElse(null);
        if (post == null) {
            return null; // Handle post not found scenario
        }

        List<String> contributionContents = new ArrayList<>();
        if(!contributionContents.isEmpty()){
            for (Contribution contribution : post.getContributions()) {
                contributionContents.add(contribution.getContent());
            }
        }
        PostResponse postResponse = new PostResponse(
                        post.getId(),
                        post.getStatus(),
                        post.getTitle(),
                        post.getContent(),
                        post.getUsername(),
                        post.getLocalDate(),
                        post.getComments(),
                        contributionContents,
                        post.getCategories()
                );
        return postResponse;
    }

    public PostResponse createPost(PostRequest postRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setUsername(username);
        post.setStatus(Post.PostStatus.APPROVED);
        List<Category.ECategory> strCategories = postRequest.getCategories();

        if (strCategories != null && strCategories.isEmpty()) {
            Category postCategory = (Category) categoryRepository.findByName(Category.ECategory.CATEGORY_OTHER)
                    .orElseThrow(() -> {
                        logger.error("Category is not found");
                        return new RuntimeException("Error: Category is not found.");
                    });
            logger.info("The post has been assigned the category of CATEGORY_OTHER");
            strCategories.add(postCategory.getName());
        }
        post.setCategories(strCategories); // Set the categories for the post
        post = postRepository.save(post);

        return mapToResponse(post);
    }



    public boolean deletePostById(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PostResponse mapToResponse(Post post) {
        PostResponse postResponse = new PostResponse(
                post.getId(),
                post.getStatus(),
                post.getTitle(),
                post.getContent(),
                post.getUsername(),
                post.getLocalDate(),
                post.getComments(),
                contributionContents,
                post.getCategories()
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

    public boolean approvePost(String postId) {
        Optional<Object> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = (Post) optionalPost.get();
            post.setStatus(Post.PostStatus.APPROVED);
            postRepository.save(post);
            return true;
        } else {
            return false;
        }
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

    public List<PostResponse> searchPosts(String searchTerm) {
        // Search for posts based on the search term
        List<Post> matchingPosts = postRepository.findByUsernameContainingIgnoreCaseAndTitleContainingIgnoreCaseAndCategoriesContainingIgnoreCase(searchTerm, searchTerm);

        // Map the matching posts to PostResponse objects
        return matchingPosts.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public int getNumberOfPostsByUser(String username) {
        return postRepository.countByUsername(username);
    }

    public int getNumberOfCommentsReceivedByUser(String username) {
        List<Post> userPosts = postRepository.findByUsername(username);
        int commentCount = 0;

        for (Post post : userPosts) {
            commentCount += post.getComments().size();
        }

        return commentCount;
    }

    public int getNumberOfContributionsByUser(String username) {
        List<Contribution> userContributions = contributionRepository.findByContributorUsername(username);
        return userContributions.size();
    }

    public Long getPostCount() {
        return postRepository.count();
    }
    public Long getCommentCount() {
        return postRepository.count();
    }


}
