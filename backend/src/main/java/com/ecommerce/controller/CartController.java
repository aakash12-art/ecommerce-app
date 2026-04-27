package com.ecommerce.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartDto;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.service.CartService;
import com.ecommerce.util.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	@GetMapping
	public CartDto get() {
		return cartService.getCart(SecurityUtils.getCurrentUser());
	}

	@PostMapping("/items")
	public CartDto add(@Valid @RequestBody AddToCartRequest req) {
		return cartService.addItem(SecurityUtils.getCurrentUser(), req);
	}

	@PutMapping("/items/{productId}")
	public CartDto update(@PathVariable Long productId, @Valid @RequestBody UpdateCartItemRequest req) {
		return cartService.updateItem(SecurityUtils.getCurrentUser(), productId, req);
	}

	@DeleteMapping("/items/{productId}")
	public CartDto remove(@PathVariable Long productId) {
		return cartService.removeItem(SecurityUtils.getCurrentUser(), productId);
	}
}
