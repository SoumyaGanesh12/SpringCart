package com.ecommerce.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
	// Internal database Id
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id; 
	
	// External user Id (U0001)
	@Column(unique=true)
	private String userId;
	
	@Column(nullable=false, unique=true)
	private String email;
	
	@Column(nullable=false)
	private String password;
	
	@Column(nullable=false)
	private String firstName;
	
	@Column(nullable=false)
	private String lastName;
	
	// Optional fields - phone number and address
	private String phoneNumber;
	
	@Column(length=500)
	private String address;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private Role role = Role.CUSTOMER;
	
	@Column(nullable=false)
	private Boolean active = true;
	
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
//	@PostPersist
//	private void generateUserId() {
//		// Generate userId after database generates Id
//		this.userId = String.format("U%04d", this.id);
//	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
	
	// Enum for user roles
	public enum Role{
		CUSTOMER,
		ADMIN
	}
	
	// Helper
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	
}
