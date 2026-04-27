package com.ecommerce.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ecommerce.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

	private Long id;
	private BigDecimal totalAmount;
	private OrderStatus status;
	private Instant createdAt;
	private List<OrderItemDto> items;
}
