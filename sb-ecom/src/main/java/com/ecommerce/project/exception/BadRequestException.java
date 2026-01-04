package com.ecommerce.project.exception;

// "400 - Bad Request" scenario
public class BadRequestException extends APIException {

	public BadRequestException(String message) {
		super(message);
	}
	
}
