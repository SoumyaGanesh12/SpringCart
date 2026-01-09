package com.ecommerce.project.security;

//import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
		// Find user by email from DB
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "User not found with email: " + email
            ));

        // Wrap User into CustomUserDetails
        return new CustomUserDetails(user);
	}
}
