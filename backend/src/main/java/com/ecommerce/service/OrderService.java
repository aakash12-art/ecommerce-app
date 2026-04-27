package com.ecommerce.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;

@Service
public class OrderService {

	private final OrderRepository orderRepository;
	private final CartService cartService;
	private final UserRepository userRepository;

	public OrderService(
			OrderRepository orderRepository,
			CartService cartService,
			UserRepository userRepository) {
		this.orderRepository = orderRepository;
		this.cartService = cartService;
		this.userRepository = userRepository;
	}

	@Transactional
	public OrderDto checkout(User user) {
		var managedUser = userRepository.findById(user.getId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Cart cart = cartService.getCartOrThrow(managedUser);
		if (cart.getItems().isEmpty()) {
			throw new BadRequestException("Cannot checkout an empty cart");
		}
		Order order = new Order();
		order.setUser(managedUser);
		order.setStatus(OrderStatus.CONFIRMED);
		BigDecimal total = BigDecimal.ZERO;
		List<OrderItem> lines = new ArrayList<>();
		for (CartItem ci : cart.getItems()) {
			var product = ci.getProduct();
			BigDecimal unit = product.getPrice();
			BigDecimal lineAmount = unit.multiply(BigDecimal.valueOf(ci.getQuantity()));
			total = total.add(lineAmount);
			OrderItem oi = new OrderItem();
			oi.setOrder(order);
			oi.setProduct(product);
			oi.setQuantity(ci.getQuantity());
			oi.setPrice(unit);
			lines.add(oi);
		}
		order.setTotalAmount(total);
		order.setItems(lines);
		order = orderRepository.save(order);
		cartService.clearCart(cart);
		return toDto(orderRepository.findById(order.getId()).orElse(order));
	}

	public List<OrderDto> listForUser(User user) {
		if (user.getRole() == Role.ADMIN) {
			return orderRepository.findAllOrdersWithItems().stream().map(this::toDto).toList();
		}
		return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
				.map(this::toDto).toList();
	}

	public OrderDto getById(User user, Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
		if (user.getRole() != Role.ADMIN && !order.getUser().getId().equals(user.getId())) {
			throw new ResourceNotFoundException("Order not found: " + orderId);
		}
		return toDto(order);
	}

	private OrderDto toDto(Order o) {
		List<OrderItemDto> items = o.getItems().stream()
				.map(li -> new OrderItemDto(
						li.getProduct().getId(),
						li.getProduct().getName(),
						li.getQuantity(),
						li.getPrice()))
				.toList();
		return new OrderDto(o.getId(), o.getTotalAmount(), o.getStatus(), o.getCreatedAt(), items);
	}
}
