package com.mayur.DesiCart.shop.product.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayur.DesiCart.shop.product.dto.AddProductRequest;
import com.mayur.DesiCart.shop.product.dto.ProductDto;
import com.mayur.DesiCart.shop.product.dto.ProductMapper;
import com.mayur.DesiCart.shop.product.dto.ProductUpdateRequest;
import com.mayur.DesiCart.shop.product.exception.ProductAlreadyPresentException;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.product.models.Category;
import com.mayur.DesiCart.shop.product.models.Product;
import com.mayur.DesiCart.shop.product.repositories.CategoryRepository;
import com.mayur.DesiCart.shop.product.repositories.ProductRepository;
import com.mayur.DesiCart.shop.product.utils.ProductUtils;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductUtils productUtils;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;


    private static final String PRODUCT_CACHE_KEY = "products:category";

    @Override
    @CacheEvict(value = "productsByCategory", allEntries = true)
    public ProductDto createProduct(AddProductRequest request) {
        productUtils.validateAddProductRequest(request);

        if (productExists(request.getName(), request.getBrand())){
            throw new ProductAlreadyPresentException(request.getName(), request.getBrand());

        }

        // Business logic: save category as new category if you have not seen this category ever
        // Fetch or create category by name
        Category category = categoryRepository
                .findFirstByNameIgnoreCase(request.getCategory().getName())
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .name(request.getCategory().getName().trim())
                            .build();
                    return categoryRepository.save(newCategory);
                });

        // Now build the product
        Product product = Product.builder()
                .name(request.getName())
                .brand(request.getBrand())
                .price(request.getPrice())
                .inventory(request.getInventory())
                .description(request.getDescription())
                .category(category)
                .build();
        return productMapper.productToDto(productRepository.save(product));

    }

    @Override
    @CacheEvict(
            value = { "products", "product-search", "product-category" },
            allEntries = true
    )
    @Transactional
    public ProductDto updateProduct(Long productId, ProductUpdateRequest request) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId must not be null");
        }

        if (request == null) {
            throw new IllegalArgumentException("Update request must not be null");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + productId));

        // Name + Brand uniqueness check
        if (request.getName() != null && request.getBrand() != null) {
            boolean exists =
                    productRepository.existsByNameAndBrand(
                            request.getName(), request.getBrand());

            if (exists &&
                    (!request.getName().equals(product.getName()) ||
                            !request.getBrand().equals(product.getBrand()))) {

                throw new ProductAlreadyPresentException(request.getName(), request.getBrand());
            }
        }

        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }

        if (request.getBrand() != null) {
            product.setBrand(request.getBrand().trim());
        }

        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            product.setPrice(request.getPrice());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        // Category handling
        if (request.getCategory() != null &&
                request.getCategory().getName() != null) {

            Category category = categoryRepository
                    .findByName(request.getCategory().getName())
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName(request.getCategory().getName());
                        return categoryRepository.save(newCategory);
                    });

            product.setCategory(category);
        }
        Product updated = productRepository.save(product);

        return productMapper.productToDto(updated);
    }

    @Override
    public ProductDto getProductById(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("productId must not be null");
        }
        Product product = productRepository.getReferenceById(productId);
        return productMapper.productToDto(product);
    }

    @Override
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null");
        }
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(productMapper::productToDto);
    }

    @Override
    @Cacheable(
            value = "productsByCategory",
            key = "#categoryName + ':' + #pageable.pageNumber + ':' + #pageable.pageSize"
    )
    public Page<ProductDto> getProductsByCategory(String categoryName, Pageable pageable) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            throw new IllegalArgumentException("Category name must not be null or empty");
        }

        if (pageable == null) {
            pageable = PageRequest.of(0, 20); // default page if not provided
        }

        // fetch from dB if not cached
        Page<Product> productPage = productRepository.findByCategoryName(categoryName, pageable);
        // map to dto
        Page<ProductDto> dtoPage = productPage.map(productMapper::productToDto);

        // store in redis with ttl
        return dtoPage;
    }

    @Override
    public Page<ProductDto> getProductsByBrand(String brand, Pageable pageable) {
        Page<Product> products = productRepository.findByBrand(brand, pageable);
        return products.map(productMapper::productToDto);
    }

    @Override
    public Page<ProductDto> getProductsByCategoryAndBrand(String categoryName, String brand, Pageable pageable) {
        Page<Product> products = productRepository.findByCategoryNameAndBrand(categoryName, brand, pageable);
        return products.map(productMapper::productToDto);
    }

    @Override
    @Cacheable(
            cacheNames = "productsByName",
            key = "#name + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort"
    )
    public Page<ProductDto> searchProductsByName(String name, Pageable pageable) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Search name must not be empty");
        }

        Page<Product> products = productRepository.findByName(name, pageable);
        return products.map(productMapper::productToDto);
    }

    @Override
    @Transactional
    public void updateInventory(Long productId, Integer inventory) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id must not be null");
        }

        if (inventory == null) {
            throw new IllegalArgumentException("Inventory must not be null");
        }

        if (inventory < 0) {
            throw new IllegalArgumentException("Inventory cannot be negative");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Product not found with id: " + productId));

        product.setInventory(inventory);

        productRepository.save(product);

    }

    @Override
    @CacheEvict(
            value = { "products", "product-search", "product-category" },
            allEntries = true
    )
    @Transactional
    public void deleteProduct(Long productId) {
        if (productId == null){
            throw new IllegalArgumentException("Product id must not be null");
        }
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }

    public boolean productExists(String name, String brand){
        if (productUtils.isBlank(name) ||  productUtils.isBlank(brand)){
            return false;
        }
        return productRepository.existsByNameAndBrand(name, brand);
    }

    @Override
    public long countProductsByBrandAndName(String brand, String name) {
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Brand must not be null or empty");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or empty");
        }
        return productRepository.countByBrandAndName(brand.trim(), name.trim());
    }


}
