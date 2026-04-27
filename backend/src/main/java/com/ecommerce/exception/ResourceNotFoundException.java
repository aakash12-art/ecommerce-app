package com.ecommerce.exception;

/** Thrown when an entity is missing (404). */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
