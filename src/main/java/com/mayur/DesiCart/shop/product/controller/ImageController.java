package com.mayur.DesiCart.shop.product.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.mayur.DesiCart.shop.product.dto.ImageDto;
import com.mayur.DesiCart.shop.product.models.Image;
import com.mayur.DesiCart.shop.product.services.ImageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/images")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @GetMapping("/{id}")
    public ResponseEntity<ImageDto> getImageById(@PathVariable Long id) {
        Image image = imageService.getImageById(id);
        ImageDto dto = new ImageDto(image.getId(), image.getFileName(), image.getDownloadUrl());
        return ResponseEntity.ok(dto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImageById(@PathVariable Long id) {
        imageService.deleteImageById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<List<ImageDto>> saveImages(
            @PathVariable Long productId,
            @RequestParam("files") List<MultipartFile> files) {

        List<ImageDto> savedImages = imageService.saveImages(productId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedImages);
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<Void> updateImage(
            @PathVariable Long imageId,
            @RequestParam("file") MultipartFile file) {

        imageService.updateImage(file, imageId);
        return ResponseEntity.noContent().build();
    }
}
