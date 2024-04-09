package com.capstone.productservice.repositories;

import com.capstone.productservice.models.Category;
import com.capstone.productservice.models.Product;
import com.capstone.productservice.projections.ProductWithIdAndTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findById(Long id);

    Optional<Product> findByTitleAndDescription(String titile,String description);

    List<Product> findByTitleContaining(String title);

    Optional<Product> findTopThreeByTitle(String title);

    Optional<Product> findByCategory(Category category);

    void deleteById(Long id);

    void deleteByTitle(String title);

    Product save(Product product);

    @Query("select p.Id as id,p.title as title from Product p where p.Id=id")
    List<ProductWithIdAndTitle> someRandomQuery(Long id);
}
