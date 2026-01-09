package com.ecommerce.project.security;

import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String jwtSecret;
	
	@Value("${jwt.expiration}")
	private long jwtExpiration;
	
	// Generate JWT token
	public String generateToken(Authentication authentication) {
		String email = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpiration);
	
		// Build and return JWT
		return Jwts.builder()
	            .subject(email)
	            .issuedAt(currentDate)
	            .expiration(expireDate)
	            .signWith(getSigningKey())
	            .compact();
	}
	
	// Get email from JWT token
	public String getEmailFromToken(String token) throws ExpiredJwtException, MalformedJwtException {
	    Claims claims = Jwts.parser()
	            .verifyWith(getSigningKey())
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	    return claims.getSubject();
	}

	
	// Validate JWT token
	public void validateTokenOrThrow(String token) throws ExpiredJwtException, MalformedJwtException {
	    Jwts.parser()
	        .verifyWith(getSigningKey())
	        .build()
	        .parseSignedClaims(token); // will throw ExpiredJwtException if expired
	}
	
	// Get signing key from secret
	private SecretKey getSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
