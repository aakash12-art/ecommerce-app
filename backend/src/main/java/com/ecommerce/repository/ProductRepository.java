package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = "category")
	@Override
	List<Product> findAll();

	@EntityGraph(attributePaths = "category")
	@Override
	Optional<Product> findById(Long id);

	@EntityGraph(attributePaths = "category")
	List<Product> findByCategoryId(Long categoryId);
}
