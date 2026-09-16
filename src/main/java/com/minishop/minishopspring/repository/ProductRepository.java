package com.minishop.minishopspring.repository;

import com.minishop.minishopspring.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
