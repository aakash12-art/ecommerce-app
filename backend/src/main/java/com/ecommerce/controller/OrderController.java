package com.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.service.OrderService;
import com.ecommerce.util.SecurityUtils;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/checkout")
	@ResponseStatus(HttpStatus.CREATED)
	public OrderDto checkout() {
		return orderService.checkout(SecurityUtils.getCurrentUser());
	}

	@GetMapping
	public List<OrderDto> list() {
		return orderService.listForUser(SecurityUtils.getCurrentUser());
	}

	@GetMapping("/{id}")
	public OrderDto get(@PathVariable Long id) {
		return orderService.getById(SecurityUtils.getCurrentUser(), id);
	}
}
