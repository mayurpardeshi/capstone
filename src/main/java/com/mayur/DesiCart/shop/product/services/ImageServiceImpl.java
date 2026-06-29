package com.mayur.DesiCart.shop.product.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mayur.DesiCart.shop.product.dto.ImageDto;
import com.mayur.DesiCart.shop.product.dto.ProductDto;
import com.mayur.DesiCart.shop.product.dto.ProductMapper;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.product.models.Image;
import com.mayur.DesiCart.shop.product.models.Product;
import com.mayur.DesiCart.shop.product.repositories.ImageRepository;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService{
    private final ImageRepository imageRepository;
    private final ProductService productService;
    private final ProductMapper productMapper;
    @Override
    public Image getImageById(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Image ID can't  be null");
        }
        return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Image not found with id: "+id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository::delete, () -> {
                    throw new ResourceNotFoundException("Delete of image failed, Image not found with Id"+id);
                });

    }

    @Override
    public List<ImageDto> saveImages(Long productId, List<MultipartFile> files) {

        ProductDto productDto = productService.getProductById(productId);
        Product product = productMapper.dtoToProduct(productDto);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file: files){
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);
                image.setDownloadUrl(createDownloadUri(image));
                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl(createDownloadUri(savedImage));
                imageRepository.save(savedImage);

                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);

            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;

    }

    private String createDownloadUri(Image image){
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/images/")
                .path(image.getId().toString())
                .toUriString();
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        Image image = getImageById(imageId);
        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}
