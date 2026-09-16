package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.ProductRequestDTO;
import com.minishop.minishopspring.dto.ProductResponseDTO;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.exception.ProductNotFoundException;
import com.minishop.minishopspring.mapper.ProductMapper;
import com.minishop.minishopspring.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;



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


        oldProduct.setName(request.getName());
        oldProduct.setPrice(request.getPrice());
        oldProduct.setQuantity(request.getQuantity());

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
}