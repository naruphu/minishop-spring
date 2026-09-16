package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.ProductRequestDTO;
import com.minishop.minishopspring.dto.ProductResponseDTO;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getProduct(){

        List<ProductResponseDTO> products =
                productService.getAllProduct();

        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id
    ){

        ProductResponseDTO product =
                productService.getProductById(id);


        return ResponseEntity.ok(product);

    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid
            @RequestBody ProductRequestDTO request
    ){

        ProductResponseDTO response =
                productService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody @Valid ProductRequestDTO request
    ){

        ProductResponseDTO product =
                productService.updateProduct(id, request);


        return ResponseEntity.ok(product);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ){

        productService.deleteProduct(id);


        return ResponseEntity.noContent().build();

    }
}
