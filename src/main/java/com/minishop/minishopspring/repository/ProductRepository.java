package com.minishop.minishopspring.repository;

import com.minishop.minishopspring.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCaseAndCategory_Id(
            String keyword,
            Integer categoryId,
            Pageable pageable
    );
    Page<Product> findByCategory_Id(Integer categoryId, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(
            String keyword,
            Pageable pageable
    );

    Page<Product> findByCategory_IdAndActiveTrue(
            Integer categoryId,
            Pageable pageable
    );

    Page<Product> findByNameContainingIgnoreCaseAndCategory_IdAndActiveTrue(
            String keyword,
            Integer categoryId,
            Pageable pageable
    );

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Query(
            value = "SELECT * FROM product WHERE id = :id FOR UPDATE",
            nativeQuery = true
    )
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

}
