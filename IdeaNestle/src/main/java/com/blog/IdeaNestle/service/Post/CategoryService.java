package com.blog.IdeaNestle.service.Post;

import com.blog.IdeaNestle.repository.CategoryRepository;
import com.blog.IdeaNestle.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository; // Assuming you have a PostRepository

}
