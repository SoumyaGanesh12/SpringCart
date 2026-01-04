package com.ecommerce.project.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
