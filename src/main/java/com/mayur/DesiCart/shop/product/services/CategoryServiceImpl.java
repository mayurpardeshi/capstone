package com.mayur.DesiCart.shop.product.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.product.exception.CategoryAlreadyPresent;
import com.mayur.DesiCart.shop.product.exception.ProductAlreadyPresentException;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.product.models.Category;
import com.mayur.DesiCart.shop.product.repositories.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    @Override
    public Category getCategoryById(Long id) {
        if (id == null){
            throw new IllegalArgumentException("CategoryId can't be null");
        }

        return categoryRepository.findCategoryById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found for id: "+id));
    }

    @Override
    public Category getCategoryByName(String name) {
        if (name == null){
            throw new IllegalArgumentException("Category name can't be null");
        }
        return categoryRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("No category present in dB with "+name));
    }

    @Override
    public Page<Category> getAllCategories(Pageable pageable) {
        if (pageable == null){
            throw new IllegalArgumentException(" Pageable is null");
        }
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                .filter(cat -> categoryRepository.findByName(category.getName()) == null)
                .map(categoryRepository::save)
                .orElseThrow(() -> new CategoryAlreadyPresent("Category is already present"));
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        if (category.getName() == null && category.getDescription() == null){
            throw new IllegalArgumentException("Category name and description can't be null at the same time while updating the category");
        }
        return Optional.ofNullable(getCategoryById(id))
                .map(existingCategory -> {
                    if (category.getName() != null) existingCategory.setName(category.getName());
                    if (category.getDescription() != null) existingCategory.setDescription(category.getDescription());
                    return categoryRepository.save(existingCategory);
                }).orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: "+id));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findCategoryById(id)
                .ifPresentOrElse(categoryRepository::delete, () -> {
                    throw new ResourceNotFoundException("Delete failed as Category not found with id"+id);
                });

    }
}
