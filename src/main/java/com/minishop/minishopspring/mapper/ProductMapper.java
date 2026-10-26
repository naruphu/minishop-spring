package com.minishop.minishopspring.mapper;

import com.minishop.minishopspring.dto.ProductRequestDTO;
import com.minishop.minishopspring.dto.ProductResponseDTO;
import com.minishop.minishopspring.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    // Entity -> Response DTO
    public ProductResponseDTO toDTO(Product product){

        ProductResponseDTO dto = new ProductResponseDTO();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategoryName(product.getCategory().getName());

        return dto;
    }

    // Request DTO -> Entity
    public Product toEntity(ProductRequestDTO request){

        Product product = new Product();

        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        return product;
    }
}
