package com.ecommerce.project.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
	private String userId;
	private String email;
	private String firstname;
	private String lastname;
	private String phonenumber;
	private String address;
	private String role;
	private Boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
