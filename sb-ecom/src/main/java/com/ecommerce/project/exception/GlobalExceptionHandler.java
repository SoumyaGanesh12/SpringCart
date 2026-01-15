package com.ecommerce.project.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	// Handle ResourceNotFoundException (404)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(
			ResourceNotFoundException ex,
			WebRequest request){
		
		ErrorResponse errRes = new ErrorResponse(
			LocalDateTime.now(),
			ex.getMessage(),
			request.getDescription(false),
			HttpStatus.NOT_FOUND.value()
		);		
		return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
	}
	
	// Handle BadRequestException (400)
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(
			BadRequestException ex,
			WebRequest request){
		
		ErrorResponse errRes = new ErrorResponse(
			LocalDateTime.now(),
			ex.getMessage(),
			request.getDescription(false),
			HttpStatus.BAD_REQUEST.value()
		);		
		return new ResponseEntity<>(errRes, HttpStatus.BAD_REQUEST);
	}
	
	// Handle all other exceptions (500)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(
			Exception ex,
			WebRequest request){
		
		ErrorResponse errRes = new ErrorResponse(
			LocalDateTime.now(),
			ex.getMessage(),
			request.getDescription(false),
			HttpStatus.INTERNAL_SERVER_ERROR.value()
		);		
		return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	// Handle validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(
			MethodArgumentNotValidException ex,
			WebRequest request
	){
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		
		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		response.put("errors", errors);
		response.put("details", request.getDescription(false));
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
