package com.ecommerce.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ecommerce.dto.ErrorResponse;

/** Maps exceptions to consistent JSON error payloads. */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex) {
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.NOT_FOUND.value());
		body.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
		body.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> badRequest(BadRequestException ex) {
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.BAD_REQUEST.value());
		body.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
		body.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> forbidden(AccessDeniedException ex) {
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.FORBIDDEN.value());
		body.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
		body.setMessage("You do not have permission for this action");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException ex) {
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.UNAUTHORIZED.value());
		body.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
		body.setMessage("Invalid email or password");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.BAD_REQUEST.value());
		body.setError("Validation Error");
		body.setMessage("Request validation failed");
		body.setFieldErrors(fieldErrors);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> fallback(Exception ex) {
		ErrorResponse body = new ErrorResponse();
		body.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		body.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		body.setMessage(ex.getMessage() != null ? ex.getMessage() : "Unexpected error");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
	}
}
