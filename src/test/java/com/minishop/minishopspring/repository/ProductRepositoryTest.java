package com.minishop.minishopspring.repository;

import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.integration.BaseIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

    @DataJpaTest
    @ActiveProfiles("test")
    class ProductRepositoryTest extends BaseIntegrationTest {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private EntityManager entityManager;

        @Test
        void shouldSaveAndFindProduct() {

            // ARRANGE
            Product product = new Product();
            product.setName("iPhone 16");
            product.setPrice(new BigDecimal("20000000"));
            product.setQuantity(10);

            // ACT
            Product savedProduct = productRepository.save(product);

            entityManager.flush();
            entityManager.clear();

            Product foundProduct = productRepository
                    .findById(savedProduct.getId())
                    .orElseThrow();

            // ASSERT
            assertEquals("iPhone 16", foundProduct.getName());
            assertEquals(
    //                new BigDecimal("20000000.00"),
    //                foundProduct.getPrice()
                    0,
                    new BigDecimal("20000000")
                            .compareTo(foundProduct.getPrice())
            );
            assertEquals(10, foundProduct.getQuantity());
        }

        @Test
        void shouldReturnEmptyWhenProductDoesNotExist() {

            // ARRANGE
            Long nonExistingId = 999L;

            // ACT
            Optional<Product> result =
                    productRepository.findById(nonExistingId);

            // ASSERT
            assertTrue(result.isEmpty());


        }

        @Test
        void shouldReturnFirstPageOfProducts() {

            // ARRANGE
            Product p1 = new Product(new BigDecimal("20000000") , "Iphone 11", 10);
            Product p2 = new Product(new BigDecimal("30000000") , "Iphone 12", 10);
            Product p3 = new Product(new BigDecimal("40000000") , "Iphone 13", 10);
            Product p4 = new Product(new BigDecimal("50000000") , "Iphone 14", 10);
            Product p5 = new Product(new BigDecimal("60000000") , "Iphone 15", 10);

            List<Product> list = new ArrayList<>();
            list.add(p1);
            list.add(p2);
            list.add(p3);
            list.add(p4);
            list.add(p5);
            productRepository.saveAll(list);

            entityManager.flush();
            entityManager.clear();

            // ACT

            Pageable pageable = PageRequest.of(0, 2);
            Page<Product> result = productRepository.findAll(pageable);



            // ASSERT
            assertEquals(2, result.getContent().size());

            assertEquals(5, result.getTotalElements());

            assertEquals(3, result.getTotalPages());

            assertEquals(0, result.getNumber());
        }

        private Product createProduct(
                String name,
                String price,
                int quantity
        ) {
            Product product = new Product();
            product.setName(name);
            product.setPrice(new BigDecimal(price));
            product.setQuantity(quantity);

            return product;
        }
        @Test
        void shouldReturnProductsSortedByPriceDescending() {

            // ARRANGE
            Product p1 = createProduct("iPhone 11", "20000000", 10);
            Product p2 = createProduct("iPhone 12", "30000000", 10);
            Product p3 = createProduct("iPhone 13", "40000000", 10);
            Product p4 = createProduct("iPhone 14", "50000000", 10);
            Product p5 = createProduct("iPhone 15", "60000000", 10);

            productRepository.saveAll(
                    List.of(p1, p2, p3, p4, p5)
            );

            entityManager.flush();
            entityManager.clear();

            // ACT

            Pageable pageable = PageRequest.of(0, 2, Sort.by("price").descending());
            Page<Product> result = productRepository.findAll(pageable);

            // ASSERT
            assertEquals("iPhone 15", result.getContent().get(0).getName());
            assertEquals("iPhone 14", result.getContent().get(1).getName());
        }

        @Test
        void shouldFindProductsByNameIgnoringCase() {

            // ARRANGE
            Product p1 = createProduct("iPhone 11", "20000000", 10);
            Product p2 = createProduct("TV", "30000000", 10);
            Product p3 = createProduct("iPhone 13", "40000000", 10);
            Product p4 = createProduct("VSL", "50000000", 10);
            Product p5 = createProduct("iPhone 15", "60000000", 10);

            productRepository.saveAll(
                    List.of(p1, p2, p3, p4, p5)
            );

            entityManager.flush();
            entityManager.clear();

            // ACT

            Pageable pageable = PageRequest.of(0, 2);
            Page<Product> result = productRepository.findByNameContainingIgnoreCase("IPHONE", pageable);

            // ASSERT
            assertEquals(3, result.getTotalElements());
            assertEquals(2, result.getContent().size());
            assertEquals(2, result.getTotalPages());
        }

        }

