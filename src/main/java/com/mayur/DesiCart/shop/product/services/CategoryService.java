package com.mayur.DesiCart.shop.product.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mayur.DesiCart.shop.product.models.Category;

import java.util.List;

public interface CategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    Page<Category> getAllCategories(Pageable pageable);
    Category addCategory(Category category);
    Category updateCategory(Category category, Long id);
    void deleteCategory(Long id);
}
