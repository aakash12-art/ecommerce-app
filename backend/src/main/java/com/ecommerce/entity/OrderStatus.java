package com.ecommerce.entity;

/** Lifecycle of an order after checkout. */
public enum OrderStatus {
	PENDING,
	CONFIRMED,
	SHIPPED,
	DELIVERED,
	CANCELLED
}
