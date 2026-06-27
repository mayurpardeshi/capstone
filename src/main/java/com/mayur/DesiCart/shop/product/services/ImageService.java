package com.mayur.DesiCart.shop.product.services;

import org.springframework.web.multipart.MultipartFile;

import com.mayur.DesiCart.shop.product.dto.ImageDto;
import com.mayur.DesiCart.shop.product.models.Image;

import java.util.List;

public interface ImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(Long productId, List<MultipartFile> files);
    void updateImage(MultipartFile file, Long imageId);
}
