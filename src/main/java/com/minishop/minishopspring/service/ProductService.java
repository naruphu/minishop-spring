package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.ProductRequestDTO;
import com.minishop.minishopspring.dto.ProductResponseDTO;
import com.minishop.minishopspring.entity.Category;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.exception.CategoryNotFoundException;
import com.minishop.minishopspring.exception.ProductNotFoundException;
import com.minishop.minishopspring.mapper.ProductMapper;
import com.minishop.minishopspring.repository.CategoryRepository;
import com.minishop.minishopspring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;




    public List<ProductResponseDTO> getAllProduct() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(productMapper::toDTO)
                // .map(product -> productMapper.toDTO(product))
                .toList();
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()
                -> new ProductNotFoundException(
                        "Product not found with id: " + id
                    )
                );

        return productMapper.toDTO(product);
    }

    public ProductResponseDTO create(ProductRequestDTO request){
        Product product = productMapper.toEntity(request);

        Category category =
                categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() ->
                                new CategoryNotFoundException(
                                        "Category not found"
                                )
                        );

        product.setCategory(category);
        Product savedProduct = productRepository.save(product);

        return productMapper.toDTO(savedProduct);
    }


    public ProductResponseDTO updateProduct(
            Long id,
            ProductRequestDTO request
    ){

        Product oldProduct = productRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );


        oldProduct.setName(request.getName());
        oldProduct.setPrice(request.getPrice());
        oldProduct.setQuantity(request.getQuantity());
        oldProduct.setCategory(category);

        Product updatedProduct = productRepository.save(oldProduct);

        return productMapper.toDTO(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(
                        () -> new ProductNotFoundException(
                                "Product not found with id: " + id
                        )
                );

        productRepository.delete(product);
    }

    public Page<ProductResponseDTO> getProduct(int page, int size, String keyword, Integer categoryId){
        Page<Product> products;
        Pageable pageable = PageRequest.of(page, size);

        if((keyword != null && !keyword.trim().isEmpty()) && categoryId != null){

            products = productRepository
                    .findByNameContainingIgnoreCaseAndCategory_Id(
                            keyword,
                            categoryId,
                            pageable
                    );

        }
        else if(keyword != null && !keyword.trim().isEmpty()){

            products = productRepository
                    .findByNameContainingIgnoreCase(
                            keyword,
                            pageable
                    );

        }
        else if(categoryId != null){

            products = productRepository
                    .findByCategory_Id(
                            categoryId,
                            pageable
                    );

        }
        else{

            products = productRepository.findAll(pageable);

        }


        return products.map(productMapper::toDTO);

    }

}