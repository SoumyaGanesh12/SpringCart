package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.dto.UserRequestDTO;
import com.ecommerce.project.dto.UserResponseDTO;

public interface UserService {
	// Register new user
	UserResponseDTO registerUser(UserRequestDTO userReqDTO);
	
	// Get all users
	List<UserResponseDTO> getAllUsers();
	
	// Get user by user Id
	UserResponseDTO getUserByUserId(String userId);
	
	// Update user profile
	UserResponseDTO updateUser(String userId, UserRequestDTO userReqDTO);
	
	// Delete user (soft delete - active = false)
	String deleteUser(String userId);
	
	// Activate/Deactivate user
	UserResponseDTO toggleUserStatus(String userId);	
}
