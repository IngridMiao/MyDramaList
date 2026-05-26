package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Arrays;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategoriesByUserId(Long userId) {
        List<Category> categories = categoryRepository.findByUserId(userId);
        if (categories.isEmpty()) {
            // 初始化默認分類
            List<String> defaults = Arrays.asList("長劇", "短劇", "綜藝");
            for (String name : defaults) {
                categoryRepository.save(new Category(userId, name));
            }
            return categoryRepository.findByUserId(userId);
        }
        return categories;
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}
