package com.ecommerce.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
	private String token;
	private String tokenType = "Bearer";
	private String userId;
	private String email;
	private String firstName;
	private String lastName;
	private String role;
}
