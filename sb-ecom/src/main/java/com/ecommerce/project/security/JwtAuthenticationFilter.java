package com.ecommerce.project.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private CustomUserDetailsService customUserServ;
	
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException{
		// Get JWT token from request header
		String token = getJwtFromRequest(req);
		
		// Validate token
		if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
			// Get email from token
			String email = jwtTokenProvider.getEmailFromToken(token);
			
			// Load user details from DB
			UserDetails userDetails = customUserServ.loadUserByUsername(email);
			
			// Create authentication object
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails,  // Principal (who is authenticated)
					null, // Credentials (password - not needed after validation)
					userDetails.getAuthorities() // Roles/Authorities
			);
			
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
			
			// Set authentication in Spring Security context
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		
		// Continue with request
		filterChain.doFilter(req, res);
	}
	
	private String getJwtFromRequest(HttpServletRequest req) {
		String bearerToken = req.getHeader("Authorization");
		
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			// Remove Bearer from prefix
			return bearerToken.substring(7);
		}
		return null;
	}
	
}
