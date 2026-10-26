package com.minishop.minishopspring.service;

import com.minishop.minishopspring.dto.CategoryRequestDTO;
import com.minishop.minishopspring.dto.CategoryResponseDTO;
import com.minishop.minishopspring.entity.Category;
import com.minishop.minishopspring.exception.CategoryNotFoundException;
import com.minishop.minishopspring.mapper.CategoryMapper;
import com.minishop.minishopspring.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryResponseDTO> getAllCategories(){
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }

    public CategoryResponseDTO getCategoryById(Integer id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new CategoryNotFoundException("Category not found with id: " + id)
                );
        return categoryMapper.toDTO(category);
    }

    public CategoryResponseDTO create (CategoryRequestDTO request){
        Category category = categoryMapper.toEntity(request);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    public CategoryResponseDTO updateCategory(
            Integer id,
            CategoryRequestDTO request
    ){

        Category oldCategory = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new CategoryNotFoundException("Category not found with id: " + id)
                );


        oldCategory.setName(request.getName());

        Category updatedCategory = categoryRepository.save(oldCategory);

        return categoryMapper.toDTO(updatedCategory);
    }

    public void deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()
                        -> new CategoryNotFoundException("Category not found with id: " + id)
                );

        categoryRepository.delete(category);
    }


}
