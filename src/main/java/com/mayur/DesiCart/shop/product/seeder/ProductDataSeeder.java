package com.mayur.DesiCart.shop.product.seeder;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.mayur.DesiCart.shop.product.models.Category;
import com.mayur.DesiCart.shop.product.models.Image;
import com.mayur.DesiCart.shop.product.models.Product;
import com.mayur.DesiCart.shop.product.repositories.CategoryRepository;
import com.mayur.DesiCart.shop.product.repositories.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class ProductDataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final Faker faker;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("I m RUN");
        if (productRepository.count() >0){
            return; // data is already added
        }
        seedCategoriesAndProducts();
    }

    private List<Category> seedCategories() {
        List<String> categoryNames = List.of(
                "Electronics",
                "Books",
                "Fashion",
                "Home",
                "Sports",
                "Grocery",
                "Automobiles"
        );

        List<Category> categories = categoryNames.stream()
                .map(name -> {
                    Category c = new Category();
                    c.setName(name);
                    c.setDescription(faker.lorem().sentence());
                    return c;
                })
                .toList();

        return categoryRepository.saveAll(categories);
    }

    private void seedCategoriesAndProducts() {
        List<Category> categories = seedCategories();

        List<Product> products = new ArrayList<>();

        for (int i = 0; i < 500; i++) {
            Product product = new Product();
            product.setName(faker.commerce().productName());
            product.setBrand(faker.company().name());
            product.setPrice(new BigDecimal(faker.commerce().price(100, 5000)));
            product.setInventory(faker.bool().bool() ? faker.number().numberBetween(0, 200) : null);
            product.setDescription(faker.lorem().paragraph());
            // Optional category
            if (faker.bool().bool()) {
                product.setCategory(faker.options().nextElement(categories));
            }
            // Images
            product.setImages(generateImages(product));
            products.add(product);
        }
        productRepository.saveAll(products);
    }

    private List<Image> generateImages(Product product) {
        int imageCount = faker.number().numberBetween(1, 4);
        List<Image> images = new ArrayList<>();

        for (int i = 0; i < imageCount; i++) {
            Image image = new Image();
            image.setFileName(faker.file().fileName());
            image.setFileType("image/png");
            image.setDownloadUrl("https://cdn.fake.com/" + faker.internet().uuid());
            image.setImage(null); // DO NOT store large blobs for seed data
            image.setProduct(product);
            images.add(image);
        }
        return images;
    }




}
