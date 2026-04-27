package com.ecommerce.dto;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

	private Instant timestamp = Instant.now();
	private int status;
	private String error;
	private String message;
	private Map<String, String> fieldErrors;
}
