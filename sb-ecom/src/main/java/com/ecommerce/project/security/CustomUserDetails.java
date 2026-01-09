package com.ecommerce.project.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.project.model.User;

public class CustomUserDetails implements UserDetails {
	private final User user;
	
	public CustomUserDetails(User user) {
		this.user = user;
	}
	
	// Access user
	public User getUser() {
		return this.user;
	}
	
	// Custom getter to access userId
	public Long getId() {
		return user.getId();
	}
	
	// Custom getter for active status
	public boolean isActive() {
		return user.getActive();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities(){
		return Collections.singletonList(
				new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())
		);
	}
	
	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	@Override
	public String getPassword() {
		return user.getPassword();
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return user.getActive();
	}
}
