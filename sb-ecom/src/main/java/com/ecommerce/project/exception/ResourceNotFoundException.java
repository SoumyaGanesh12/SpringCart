package com.ecommerce.project.exception;

// "404 - Not Found" scenario
public class ResourceNotFoundException extends APIException{
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
