package com.ecommerce.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="carts")
@Data
@NoArgsConstructor
public class Cart {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartId;
	
	// One cart belongs to one user
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	
	// One cart has many items
	@OneToMany(mappedBy="cart", cascade=CascadeType.ALL, orphanRemoval=true)
	private List<CartItem> cartItems = new ArrayList<>();
	
	@Column(nullable=false, precision=10, scale=2)
	private BigDecimal totalAmount = BigDecimal.ZERO;
	
	@Column(nullable=false, updatable=false)
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}
	
	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}
	
	// Helper method : Add item to cart
	public void addCartItem(CartItem item) {
		cartItems.add(item);
		item.setCart(this);
		calculateTotalAmount();
	}
	
	public void removeCartItem(CartItem item) {
		cartItems.remove(item);
		item.setCart(null);
		calculateTotalAmount();
	}
	
	public void calculateTotalAmount() {
		this.totalAmount = cartItems.stream()
			.map(CartItem::getSubtotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
