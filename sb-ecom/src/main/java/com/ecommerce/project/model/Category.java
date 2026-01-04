package com.ecommerce.project.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity // jpa identifies this as a table
//@Entity(name = "categories") // to specify tablename, default - classname becomes the tablename
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long categoryId;
	
	@Column(nullable = false, unique = true)
	private String categoryName;
	
	private String description;
	
	// One category has MANY products
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Product> products = new ArrayList<>();
	
//	public Category(Long categoryId, String categoryName) {
//		super();
//		this.categoryId = categoryId;
//		this.categoryName = categoryName;
//	}
//
//	public Category() {
//	
//	}
//	
//	public Long getCategoryId() {
//		return categoryId;
//	}
//
//	public void setCategoryId(Long categoryId) {
//		this.categoryId = categoryId;
//	}
//
//	public String getCategoryName() {
//		return categoryName;
//	}
//
//	public void setCategoryName(String categoryName) {
//		this.categoryName = categoryName;
//	}
	
}
