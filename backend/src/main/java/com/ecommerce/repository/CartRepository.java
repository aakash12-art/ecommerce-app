package com.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@EntityGraph(attributePaths = { "items", "items.product", "items.product.category" })
	Optional<Cart> findByUserId(Long userId);
}
