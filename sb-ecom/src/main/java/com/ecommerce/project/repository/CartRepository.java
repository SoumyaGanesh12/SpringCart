package com.ecommerce.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>{
	// Find cart by user Id
	Optional<Cart> findByUserId(Long userId);
	
	// Find cart by user's custom Id (U0001)
	Optional<Cart> findByUserUserId(String userId);
}
