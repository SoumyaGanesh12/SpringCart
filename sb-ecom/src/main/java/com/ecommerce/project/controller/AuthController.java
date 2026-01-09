package com.ecommerce.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.project.dto.LoginRequestDTO;
import com.ecommerce.project.dto.LoginResponseDTO;
import com.ecommerce.project.dto.UserRequestDTO;
import com.ecommerce.project.dto.UserResponseDTO;
import com.ecommerce.project.exception.BadRequestException;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import com.ecommerce.project.security.JwtTokenProvider;
import com.ecommerce.project.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private AuthenticationManager authMngr;
	
	@Autowired
	private JwtTokenProvider jwtTokenProv;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
    private UserService userService;
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginReq){
		try {
			// Authenticate user
			Authentication authentication = authMngr.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginReq.getEmail(),
							loginReq.getPassword()
					)
			);
			
			// Generate jwt token
			String token = jwtTokenProv.generateToken(authentication);
			
			// Get user details
			User user = userRepo.findByEmail(loginReq.getEmail())
				.orElseThrow(() -> new BadRequestException("User not found"));
			
			// Create response
			LoginResponseDTO response = new LoginResponseDTO(
					token,
					"Bearer",
					user.getUserId(),
					user.getEmail(),
					user.getFirstName(),
					user.getLastName(),
					user.getRole().toString()
			);
			
			return ResponseEntity.ok(response);
		}catch(AuthenticationException e) {
			throw new BadRequestException("Invalid email or password");
		}
	}
	
	// Register new user
	@PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(
            @RequestBody UserRequestDTO userRequestDTO) {
        
        UserResponseDTO registeredUser = userService.registerUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}
