package com.capstone.productservice.repositories;

import com.capstone.productservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryReporsitory extends JpaRepository<Category,Long> {
    Category save(Category category);
}
