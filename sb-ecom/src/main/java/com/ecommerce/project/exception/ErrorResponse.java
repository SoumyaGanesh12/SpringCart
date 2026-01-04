package com.ecommerce.project.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Standard response format

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private LocalDateTime timestamp;
	private String message;
	private String details;
	private int status;
}
