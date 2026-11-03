    package com.minishop.minishopspring.controller;

    import com.minishop.minishopspring.dto.ProductResponseDTO;
    import com.minishop.minishopspring.service.ProductService;
    import org.springframework.data.domain.Page;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/products")
    public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
            this.productService = productService;
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductResponseDTO> getProductById(
                @PathVariable Long id
        ) {

            ProductResponseDTO product =
                    productService.getActiveProductById(id);

            return ResponseEntity.ok(product);
        }

        @GetMapping
        public ResponseEntity<Page<ProductResponseDTO>> getProducts(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size,
                @RequestParam(required = false) String keyword,
                @RequestParam(required = false) Integer categoryId
        ) {

            Page<ProductResponseDTO> products =
                    productService.getActiveProducts(
                            page,
                            size,
                            keyword,
                            categoryId
                    );

            return ResponseEntity.ok(products);
        }
    }