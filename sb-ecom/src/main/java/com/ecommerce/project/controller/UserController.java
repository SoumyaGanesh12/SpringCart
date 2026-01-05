package com.ecommerce.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.UserRequestDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
	@Autowired
	private UserService userService;
	
	// Register new user
	@PostMapping("/public/users/register")
	public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserRequestDTO dto){
		UserResponseDTO regUser = userService.registerUser(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(regUser);
	}
	
	// Get all users
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
		List<UserResponseDTO> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
	
	// Get user by user id
	@GetMapping("/public/users/{userId}")
	public ResponseEntity<UserResponseDTO> getUserByUserId(@PathVariable String userId){
		UserResponseDTO updatedUser = userService.getUserByUserId(userId);
		return ResponseEntity.ok(updatedUser);
	}
	
	// Update user profile
	@PutMapping("/public/users/{userId}")
	public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String userId, @RequestBody UserRequestDTO dto){
		UserResponseDTO updatedUser = userService.updateUser(userId, dto);
		return ResponseEntity.ok(updatedUser);
	}
	
	// Delete user (soft delete)
	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable String userId){
		String message = userService.deleteUser(userId);
		return ResponseEntity.ok(message);
	}
	
	// Toggle user active status
	@PatchMapping("/admin/users/{userId}/toggle-status")
	public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable String userId){
		UserResponseDTO user = userService.toggleUserStatus(userId);
		return ResponseEntity.ok(user);
	}
	
}
