package com.minishop.minishopspring.mapper;

import com.minishop.minishopspring.dto.CategoryRequestDTO;
import com.minishop.minishopspring.dto.CategoryResponseDTO;
import com.minishop.minishopspring.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toDTO (Category category){
        CategoryResponseDTO response = new CategoryResponseDTO();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }

    public Category toEntity(CategoryRequestDTO request){
        Category category = new Category();
        category.setName(request.getName());
        return category;
    }
}
