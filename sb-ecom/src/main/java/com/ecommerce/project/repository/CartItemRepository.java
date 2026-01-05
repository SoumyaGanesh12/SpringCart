package com.ecommerce.project.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.project.model.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    // Find cart item by cart and product
	Optional<CartItem> findByCartCartIdAndProductProductId(Long cartId, Long productId);
}
