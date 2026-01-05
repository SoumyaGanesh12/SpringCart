package com.ecommerce.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.project.dto.UserRequestDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	@Autowired
	private UserRepository userRepo;
	
	// Register new user
	@Override
	public UserResponseDTO registerUser(UserRequestDTO userReqDTO) {
		if(userRepo.existsByEmail(userReqDTO.getEmail())) {
			throw new BadRequestException(
				"Email '" + userReqDTO.getEmail() +"' is already registered"
			);
		}
		
		// Convert DTO to Entity
		User user = convertToEntity(userReqDTO);
		
		// Save user
		User saveduser = userRepo.save(user);

	    saveduser.setUserId(String.format("U%04d", saveduser.getId()));  // Generate userId
	    saveduser = userRepo.save(saveduser);  // Second save
		
		// Convert Entity to Response DTO
		return convertToResponseDTO(saveduser);
	}
		
	// Get all users
	@Override
	public List<UserResponseDTO> getAllUsers(){
		List<User> users = userRepo.findAll();
		
		return users.stream()
			.map(this::convertToResponseDTO)
			.collect(Collectors.toList());
	}
	
	// Get user by user Id
	@Override
	public UserResponseDTO getUserByUserId(String userId) {
		User user = userRepo.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"User with Id " + userId + " not found"
		));
		
		return convertToResponseDTO(user);
	}
	
	// Update user profile
	@Override
	public UserResponseDTO updateUser(String userId, UserRequestDTO userReqDTO) {
		// Find existing user in order to update
		User user = userRepo.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"User with Id " + userId + " not found"
		));
		
		// Check if new email is not registered
		if(!user.getEmail().equals(userReqDTO.getEmail())){
			if(userRepo.existsByEmail(userReqDTO.getEmail())) {
				throw new BadRequestException(
                    "Email '" + userReqDTO.getEmail() + "' is already taken"
                );
			}
		}
		
		// Update fields
		user.setEmail(userReqDTO.getEmail());
		user.setFirstName(userReqDTO.getFirstName());
		user.setLastName(userReqDTO.getLastName());
		user.setPhoneNumber(userReqDTO.getPhoneNumber());
		user.setAddress(userReqDTO.getAddress());
		
		// Only update password if provided
		if(userReqDTO.getPassword() != null && !userReqDTO.getPassword().isEmpty()) {
			user.setPassword(userReqDTO.getPassword());
		}
		
		// Save updated password
		User updatedUser = userRepo.save(user);
		
		return convertToResponseDTO(updatedUser);
	}
	
	// Delete user (soft delete - active = false)
	@Override
	public String deleteUser(String userId) {
		User user = userRepo.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"User with Id " + userId + " not found"
		));
		
		// Soft delete - set active to false
		user.setActive(false);
		userRepo.save(user);
		
		return "User with Id " + userId + " has been deactivated";
	}
	
	// Activate/Deactivate user
	@Override
	public UserResponseDTO toggleUserStatus(String userId) {
		User user = userRepo.findByUserId(userId)
			.orElseThrow(() -> new ResourceNotFoundException(
				"User with Id " + userId + " not found"
		));
		
		// Toggle active status
		user.setActive(!user.getActive());
		User updatedUser = userRepo.save(user);
		
		return convertToResponseDTO(updatedUser);
	}
	
	// Helper Methods
	// Convert Request DTO to Entity
	private User convertToEntity(UserRequestDTO dto) {
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword(dto.getPassword());
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setPhoneNumber(dto.getPhoneNumber());
		user.setAddress(dto.getAddress());
		user.setRole(User.Role.CUSTOMER);
		user.setActive(true);
		
		return user;
	}
	
	// Convert Entity to Response DTO
	private UserResponseDTO convertToResponseDTO(User user) {
		return new UserResponseDTO(
			user.getUserId(),
			user.getEmail(),
			user.getFirstName(),
			user.getLastName(),
			user.getPhoneNumber(),
			user.getAddress(),
			user.getRole().toString(),
			user.getActive(),
			user.getCreatedAt(),
			user.getUpdatedAt()
		);
	}
}
