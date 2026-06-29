package com.mayur.DesiCart.shop.product.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mayur.DesiCart.shop.product.models.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long id);
}
