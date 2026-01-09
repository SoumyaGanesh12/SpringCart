package com.ecommerce.project.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

		// Extract JWT token from request header
	    String token = getJwtFromRequest(request);

	    if (StringUtils.hasText(token)) {
	        try {
	            // Validate token first (will throw if expired/invalid)
	            jwtTokenProvider.validateTokenOrThrow(token);

	            // Extract email
	            String email = jwtTokenProvider.getEmailFromToken(token);

	            // Load user
	            CustomUserDetails userDetails = (CustomUserDetails) customUserServ.loadUserByUsername(email);

	            // Create authentication object
	            UsernamePasswordAuthenticationToken authentication =
	                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	            
	            // Set authentication in Spring Security context
	            SecurityContextHolder.getContext().setAuthentication(authentication);

	        } catch (ExpiredJwtException ex) {
	        	// Token expired
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\":\"Token has expired. Please login again.\"}");
	            return;
	        } catch (MalformedJwtException | UnsupportedJwtException ex) {
	        	// Malformed token
	        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\":\"Invalid token.\"}");
	            return;
	        } catch (UsernameNotFoundException ex) {
	        	// User not found or deactivated 
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\":\"" + ex.getMessage() + "\"}");
	            return;
	        } catch (Exception ex) {
	        	// Other authentication errors
	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\":\"Access denied.\"}");
	            return;
	        }
	    }

	    filterChain.doFilter(request, response);
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
