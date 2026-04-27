package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.AddToCartRequest;
import com.ecommerce.dto.CartDto;
import com.ecommerce.dto.CartItemDto;
import com.ecommerce.dto.UpdateCartItemRequest;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;

@Service
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	public CartService(
			CartRepository cartRepository,
			CartItemRepository cartItemRepository,
			ProductRepository productRepository,
			UserRepository userRepository) {
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
	}

	public CartDto getCart(User user) {
		Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
		if (cart == null || cart.getItems().isEmpty()) {
			return new CartDto(List.of(), BigDecimal.ZERO);
		}
		return toDto(cart);
	}

	@Transactional
	public CartDto addItem(User user, AddToCartRequest req) {
		Product product = productRepository.findById(req.getProductId())
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + req.getProductId()));
		Cart cart = getOrCreateCartEntity(user);
		CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);
		if (existing != null) {
			existing.setQuantity(existing.getQuantity() + req.getQuantity());
		}
		else {
			CartItem line = new CartItem();
			line.setCart(cart);
			line.setProduct(product);
			line.setQuantity(req.getQuantity());
			cart.getItems().add(line);
		}
		cartRepository.save(cart);
		return toDto(cartRepository.findByUserId(user.getId()).orElse(cart));
	}

	@Transactional
	public CartDto updateItem(User user, Long productId, UpdateCartItemRequest req) {
		Cart cart = cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new BadRequestException("Cart is empty"));
		CartItem line = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
				.orElseThrow(() -> new ResourceNotFoundException("Item not in cart"));
		line.setQuantity(req.getQuantity());
		cartRepository.save(cart);
		return toDto(cartRepository.findByUserId(user.getId()).orElse(cart));
	}

	@Transactional
	public CartDto removeItem(User user, Long productId) {
		Cart cart = cartRepository.findByUserId(user.getId()).orElse(null);
		if (cart == null) {
			return new CartDto(List.of(), BigDecimal.ZERO);
		}
		cartItemRepository.findByCartIdAndProductId(cart.getId(), productId).ifPresent(cartItemRepository::delete);
		cartRepository.save(cart);
		return cartRepository.findByUserId(user.getId()).map(this::toDto)
				.orElse(new CartDto(List.of(), BigDecimal.ZERO));
	}

	@Transactional
	public void clearCart(Cart cart) {
		cart.getItems().clear();
		cartRepository.save(cart);
	}

	/** Used by checkout — returns managed cart with lines. */
	@Transactional(readOnly = true)
	public Cart getCartOrThrow(User user) {
		return cartRepository.findByUserId(user.getId())
				.orElseThrow(() -> new BadRequestException("Cart is empty"));
	}

	private Cart getOrCreateCartEntity(User user) {
		var managed = userRepository.findById(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return cartRepository.findByUserId(managed.getId()).orElseGet(() -> {
			Cart c = new Cart();
			c.setUser(managed);
			c.setItems(new ArrayList<>());
			return cartRepository.save(c);
		});
	}

	private CartDto toDto(Cart cart) {
		BigDecimal total = BigDecimal.ZERO;
		List<CartItemDto> lines = new ArrayList<>();
		for (CartItem li : cart.getItems()) {
			Product p = li.getProduct();
			BigDecimal unit = p.getPrice();
			BigDecimal lineTotal = unit.multiply(BigDecimal.valueOf(li.getQuantity()));
			total = total.add(lineTotal);
			lines.add(new CartItemDto(
					p.getId(),
					p.getName(),
					li.getQuantity(),
					unit,
					lineTotal));
		}
		return new CartDto(lines, total);
	}
}
