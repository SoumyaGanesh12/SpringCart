package com.ecommerce.project.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ecommerce.project.dto.UserRequestDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
	@Mock
	private UserRepository userRepo;
	
	@Mock
	private PasswordEncoder passwordEncoder;
	
	@InjectMocks
	private UserServiceImpl userServ;
	
	private User user1;
	private User user2;
	private UserRequestDTO userReqDTO;
	
	@BeforeEach
	public void setUp() {
		user1 = new User();
		user1.setId(1L);
		user1.setUserId("U0001");
		user1.setEmail("john@example.com");
		user1.setPassword("$2a$10$encryptedPassword");
		user1.setFirstName("John");
		user1.setLastName("Mark");
		user1.setPhoneNumber("1234567890");
		user1.setAddress("123 Main St");
        user1.setRole(User.Role.CUSTOMER);
        user1.setActive(true);
		
        user2 = new User();
        user2.setId(2L);
		user2.setUserId("U0002");
		user2.setEmail("peter@example.com");
		user2.setPassword("$2a$10$encryptedPassword");
		user2.setFirstName("Peter");
		user2.setLastName("Smith");
		user2.setPhoneNumber("1234567980");
		user2.setAddress("456 Main St");
        user2.setRole(User.Role.CUSTOMER);
        user2.setActive(true);
        
        // Create user request DTO
        userReqDTO = new UserRequestDTO();
        userReqDTO.setEmail("alice@example.com");
        userReqDTO.setPassword("pass123");
        userReqDTO.setFirstName("Alice");
        userReqDTO.setLastName("Hoe");
        userReqDTO.setPhoneNumber("9876543210");
        userReqDTO.setAddress("456 Oak Ave");
	}
	
	@Test
	public void registerUser_ShouldSuccess() {
		User savedUser = new User();
		savedUser.setId(3L);
		savedUser.setUserId("U0003");
		savedUser.setEmail("alice@example.com");
		savedUser.setPassword("$2a$10$encryptedPassword");
		savedUser.setFirstName("Alice");
		savedUser.setLastName("Hoe");
		savedUser.setPhoneNumber("9876543210");
		savedUser.setAddress("456 Oak Ave");
		savedUser.setRole(User.Role.CUSTOMER);
		savedUser.setActive(true);
		
		when(userRepo.existsByEmail("alice@example.com")).thenReturn(false);
		when(passwordEncoder.encode("pass123")).thenReturn("$2a$10$encryptedPassword");
		when(userRepo.save(any(User.class))).thenReturn(savedUser);
		
		UserResponseDTO res = userServ.registerUser(userReqDTO);
		
		assertNotNull(res);
		assertEquals("U0003", res.getUserId());
		assertEquals("alice@example.com", res.getEmail());
        assertEquals("Alice", res.getFirstname());
        assertEquals("CUSTOMER", res.getRole());
        assertTrue(res.getActive());
        
        verify(userRepo, times(1)).existsByEmail("alice@example.com");
        verify(passwordEncoder, times(1)).encode("pass123");
        verify(userRepo, times(2)).save(any(User.class));
	}
	
	@Test
	public void registerUser_ShouldFail_WhenEmailExists() {
		when(userRepo.existsByEmail("alice@example.com")).thenReturn(true);
		
		BadRequestException ex = assertThrows(
				BadRequestException.class,
				() -> userServ.registerUser(userReqDTO)
		);
		
		assertTrue(ex.getMessage().contains("already registered"));
		verify(userRepo, times(1)).existsByEmail("alice@example.com");
		verify(userRepo, never()).save(any(User.class));
	}
	
	@Test
	public void getAllUsers_ShouldReturnList() {
		when(userRepo.findAll()).thenReturn(Arrays.asList(user1, user2));
		
		List<UserResponseDTO> res = userServ.getAllUsers();
		
		assertNotNull(res);
		assertEquals(2, res.size());
		assertEquals("U0001", res.get(0).getUserId());
		assertEquals("U0002", res.get(1).getUserId());
		
		verify(userRepo, times(1)).findAll();
	}
	
	@Test
	public void getUserByUserId_ShouldReturnUser() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user1));
		
		UserResponseDTO res = userServ.getUserByUserId("U0001");
		
		assertNotNull(res);
		assertEquals("U0001", res.getUserId());
		assertEquals("john@example.com", res.getEmail());
        assertEquals("John", res.getFirstname());
        
        verify(userRepo, times(1)).findByUserId("U0001");
	}
	
	@Test
	public void getUserByUserId_ShouldFail_WhenNotFound() {
		when(userRepo.findByUserId("U9999")).thenReturn(Optional.empty());
		
		assertThrows(ResourceNotFoundException.class,
				() -> userServ.getUserByUserId("U9999"));
		
		verify(userRepo, times(1)).findByUserId("U9999");		
	}
	
	@Test
	public void updateUser_ShouldSuccess(){
		UserRequestDTO updateDTO = new UserRequestDTO();
		updateDTO.setEmail("john.updated@example.com");
        updateDTO.setPassword("newpass");
        updateDTO.setFirstName("John");
        updateDTO.setLastName("Mark Updated");
        updateDTO.setPhoneNumber("1111111111");
        updateDTO.setAddress("999 New Address");
        
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user1));
		when(userRepo.existsByEmail("john.updated@example.com")).thenReturn(false);
		when(passwordEncoder.encode("newpass")).thenReturn("$2a$10$newEncryptedPassword");
		when(userRepo.save(any(User.class))).thenReturn(user1);
		
		UserResponseDTO res = userServ.updateUser("U0001", updateDTO);
		
		assertNotNull(res);
        
        verify(userRepo, times(1)).findByUserId("U0001");
        verify(userRepo, times(1)).existsByEmail("john.updated@example.com");
        verify(passwordEncoder, times(1)).encode("newpass");
        verify(userRepo, times(1)).save(any(User.class));
	}
	
	@Test
	public void updateUser_ShouldFail_WhenEmailTaken() {
		UserRequestDTO updateDTO = new UserRequestDTO();
		updateDTO.setEmail("peter@example.com");  // Email taken by user2
        updateDTO.setPassword("peter");
        updateDTO.setFirstName("Peter");
        updateDTO.setLastName("Minn");
        
        when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user1));
        when(userRepo.existsByEmail("peter@example.com")).thenReturn(true);
        
        assertThrows(BadRequestException.class,
                () -> userServ.updateUser("U0001", updateDTO));
        
            verify(userRepo, times(1)).existsByEmail("peter@example.com");
            verify(userRepo, never()).save(any(User.class));
	}
	
	@Test
	public void deleteUser_ShouldDeactivateUser() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user1));
		when(userRepo.save(any(User.class))).thenReturn(user1);
		
		String res = userServ.deleteUser("U0001");
		
		assertTrue(res.contains("deactivated"));
		assertFalse(user1.getActive());
		
		verify(userRepo, times(1)).findByUserId("U0001");
		verify(userRepo, times(1)).save(any(User.class));
	}
	
	@Test
	public void toggleUserStatus_ShouldToggleActive() {
		when(userRepo.findByUserId("U0001")).thenReturn(Optional.of(user1));
		when(userRepo.save(any(User.class))).thenReturn(user1);
		
		boolean originalStatus = user1.getActive();
		
		UserResponseDTO res = userServ.toggleUserStatus("U0001");
		
		assertNotNull(res);
		assertEquals(!originalStatus, user1.getActive());
		
		verify(userRepo, times(1)).findByUserId("U0001");
		verify(userRepo, times(1)).save(any(User.class));
	}
}
