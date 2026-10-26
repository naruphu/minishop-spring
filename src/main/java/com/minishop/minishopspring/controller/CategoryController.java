package com.minishop.minishopspring.controller;

import com.minishop.minishopspring.dto.CategoryRequestDTO;
import com.minishop.minishopspring.dto.CategoryResponseDTO;
import com.minishop.minishopspring.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // GET /categories
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {

        List<CategoryResponseDTO> categories =
                categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    // GET /categories/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @PathVariable Integer id) {

        CategoryResponseDTO category =
                categoryService.getCategoryById(id);

        return ResponseEntity.ok(category);
    }

    // POST /categories
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO request) {

        CategoryResponseDTO category =
                categoryService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(category);
    }

    // PUT /categories/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequestDTO request) {

        CategoryResponseDTO category =
                categoryService.updateCategory(id, request);

        return ResponseEntity.ok(category);
    }

    // DELETE /categories/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Integer id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}