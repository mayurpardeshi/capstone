package com.mayur.DesiCart.shop.product.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mayur.DesiCart.shop.product.models.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsByName(String name);
    Optional<Category> findCategoryById(Long id);
    Page<Category> findAll(Pageable pageable);

    Optional<Category> findByNameIgnoreCase(String name);
    Optional<Category> findFirstByNameIgnoreCase(String name);
}
