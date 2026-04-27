package com.ecommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ecommerce.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

	@EntityGraph(attributePaths = { "items", "items.product" })
	List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

	@EntityGraph(attributePaths = { "items", "items.product" })
	Optional<Order> findById(Long id);

	/** All orders for admin dashboard (lines eagerly loaded). */
	@EntityGraph(attributePaths = { "items", "items.product" })
	@Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
	List<Order> findAllOrdersWithItems();
}
