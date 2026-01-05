package com.ecommerce.project.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="cart_items")
@Data
@NoArgsConstructor
public class CartItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cartItemId;
	
	// Many cart items belong to one cart
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="cart_id", nullable=false)
	private Cart cart;
	
	// Many cart items reference one product
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="product_id", nullable=false)
	private Product product;
	
	@Column(nullable=false)
	private Integer quantity;
	
	@Column(nullable=false, precision=10, scale=2)
	private BigDecimal price;
	
	public BigDecimal getSubtotal() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}
	
	
}
