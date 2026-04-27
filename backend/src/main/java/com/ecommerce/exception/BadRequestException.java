package com.ecommerce.exception;

/** Validation or business rule failure (400). */
public class BadRequestException extends RuntimeException {

	public BadRequestException(String message) {
		super(message);
	}
}
