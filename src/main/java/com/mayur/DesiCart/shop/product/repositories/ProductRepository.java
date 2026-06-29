package com.mayur.DesiCart.shop.product.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mayur.DesiCart.shop.product.models.Product;


public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryName(String categoryName, Pageable pageable);
    Page<Product> findByBrand(String brandName, Pageable pageable);
    Page<Product> findByCategoryNameAndBrand(String categoryName, String brandName, Pageable pageable);

    @Query("""
       SELECT p
       FROM Product p
       WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
       """)
    Page<Product> findByName(@Param("name") String name, Pageable pageable);

    Page<Product> findByBrandAndName(String brand, String name, Pageable pageable);

    Long countByBrandAndName(String brand, String name);

    boolean existsByNameAndBrand(String name, String brand);

    Product getProductById(Long productId);
}
