package com.ecommerce.project.init;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;
import com.ecommerce.project.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("dev") // runs only in dev profile
public class DataInitializer implements CommandLineRunner {
	private final UserRepository userRepo;
	private final CategoryRepository catRepo;
	private final ProductRepository prodRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public void run(String... args) {
		seedAdmin();
		seedCategories();
		seedProducts();
	}
	
	@Value("${app.admin.email}")
	private String adminEmail;
	
	@Value("${app.admin.password}")
	private String adminPassword;
	
	private void seedAdmin() {
		// Avoid duplicate admin creation
		if(userRepo.existsByEmail(adminEmail)) {
			System.out.println("Admin user exists");
			return;
		}
		
		User admin = User.builder()
					.email(adminEmail)
					.password(passwordEncoder.encode(adminPassword))
					.firstName("Admin")
					.lastName("User")
					.role(User.Role.ADMIN)
					.active(true)
					.build();

		User savedAdmin = userRepo.save(admin);

		savedAdmin.setUserId(String.format("U%04d", savedAdmin.getId()));

		userRepo.save(savedAdmin);				
	}
	
	private void seedCategories() {
		if(catRepo.count() > 0) {
			return;
		}
		
		Category electronics = new Category();
		electronics.setCategoryName("Electronics");
		electronics.setDescription("Electronic gadgets and devices");
		
		Category fashion = new Category();
		fashion.setCategoryName("Fashion");
		fashion.setDescription("Clothing and accessories");
		
		Category home = new Category();
		home.setCategoryName("Home");
		home.setDescription("Home and kitchen essentials");
		
		Category sports = new Category();
	    sports.setCategoryName("Sports");
	    sports.setDescription("Sports equipment, fitness gear and outdoor accessories");

	    Category books = new Category();
	    books.setCategoryName("Books");
	    books.setDescription("Programming, self-help and fiction books");
		
		catRepo.saveAll(List.of(electronics, fashion, home, sports, books));
	}
	
	private void seedProducts() {
		if(prodRepo.count() > 0) {
			return;
		}
		
		Category electronics = catRepo.findByCategoryName("Electronics").orElseThrow();
		Category fashion = catRepo.findByCategoryName("Fashion").orElseThrow();
		Category home = catRepo.findByCategoryName("Home").orElseThrow();
		Category sports = catRepo.findByCategoryName("Sports").orElseThrow();
		Category books = catRepo.findByCategoryName("Books").orElseThrow();
		
		List<Product> products = List.of(
				// Electronics
				createProduct("iPhone 14", "Apple smartphone with A15 chip", new BigDecimal("999.99"), 10, electronics),
		        createProduct("MacBook Air M2", "Apple lightweight laptop", new BigDecimal("1199.99"), 5, electronics),
		        createProduct("Sony WH-1000XM5", "Noise cancelling headphones", new BigDecimal("349.99"), 15, electronics),
		        createProduct("Samsung Galaxy S23", "Android flagship phone", new BigDecimal("899.99"), 12, electronics),
		        createProduct("iPad Air", "Apple tablet for productivity", new BigDecimal("599.99"), 8, electronics),
		        createProduct("Dell XPS 13", "Premium Windows laptop", new BigDecimal("1299.99"), 6, electronics),
		        createProduct("Apple Watch Series 9", "Smart wearable device", new BigDecimal("399.99"), 20, electronics),
		        createProduct("Canon DSLR Camera", "Professional photography camera", new BigDecimal("749.99"), 7, electronics),
		        createProduct("Logitech MX Master 3", "Wireless productivity mouse", new BigDecimal("99.99"), 25, electronics),
		        createProduct("Samsung 4K Monitor", "Ultra HD display", new BigDecimal("299.99"), 10, electronics),
		        
		        // Fashion
		        createProduct("Nike Air Max", "Comfortable running shoes", new BigDecimal("149.99"), 30, fashion),
		        createProduct("Adidas Hoodie", "Casual wear hoodie", new BigDecimal("79.99"), 25, fashion),
		        createProduct("Levi's Jeans", "Classic denim jeans", new BigDecimal("89.99"), 40, fashion),
		        createProduct("Puma T-Shirt", "Cotton casual t-shirt", new BigDecimal("29.99"), 50, fashion),
		        createProduct("Ray-Ban Sunglasses", "Stylish UV protection glasses", new BigDecimal("199.99"), 15, fashion),
		        createProduct("Zara Jacket", "Winter fashion jacket", new BigDecimal("129.99"), 18, fashion),
		        createProduct("Nike Sports Shorts", "Athletic wear shorts", new BigDecimal("39.99"), 35, fashion),
		        createProduct("H&M Dress", "Women's casual dress", new BigDecimal("59.99"), 22, fashion),
		        createProduct("Timberland Boots", "Durable outdoor boots", new BigDecimal("179.99"), 12, fashion),
		        createProduct("Under Armour Cap", "Sports cap", new BigDecimal("24.99"), 60, fashion),
		        
		        // Home
		        createProduct("Dyson Vacuum Cleaner", "High power cleaning device", new BigDecimal("399.99"), 10, home),
		        createProduct("Instant Pot", "Multi-cooker kitchen appliance", new BigDecimal("89.99"), 25, home),
		        createProduct("Philips Air Fryer", "Healthy cooking appliance", new BigDecimal("129.99"), 18, home),
		        createProduct("Coffee Maker", "Automatic drip coffee machine", new BigDecimal("79.99"), 20, home),
		        createProduct("Microwave Oven", "Quick heating appliance", new BigDecimal("149.99"), 15, home),
		        createProduct("Electric Kettle", "Fast boiling kettle", new BigDecimal("39.99"), 30, home),
		        createProduct("Bedside Lamp", "LED warm light lamp", new BigDecimal("19.99"), 40, home),
		        createProduct("Non-stick Cookware Set", "Kitchen cooking set", new BigDecimal("99.99"), 12, home),
		        createProduct("Air Purifier", "Clean indoor air system", new BigDecimal("199.99"), 10, home),
		        createProduct("Blender", "Smoothie and juice blender", new BigDecimal("59.99"), 22, home),
		        
		        // Sports
		        createProduct("Nike Football", "Professional football", new BigDecimal("29.99"), 50, sports),
		        createProduct("Yoga Mat", "Non-slip exercise mat", new BigDecimal("19.99"), 60, sports),
		        createProduct("Cricket Bat", "Professional wooden bat", new BigDecimal("79.99"), 20, sports),
		        createProduct("Dumbbell Set", "Home gym weights", new BigDecimal("99.99"), 15, sports),
		        createProduct("Treadmill", "Home cardio equipment", new BigDecimal("499.99"), 5, sports),
		        createProduct("Basketball", "Official size basketball", new BigDecimal("24.99"), 40, sports),
		        createProduct("Tennis Racket", "Professional racket", new BigDecimal("89.99"), 18, sports),
		        createProduct("Skipping Rope", "Fitness jump rope", new BigDecimal("9.99"), 80, sports),
		        createProduct("Sports Water Bottle", "Hydration bottle", new BigDecimal("14.99"), 70, sports),
		        createProduct("Fitness Tracker", "Activity monitoring band", new BigDecimal("59.99"), 25, sports),
		        
		        // Books
		        createProduct("Clean Code", "Programming best practices", new BigDecimal("39.99"), 30, books),
		        createProduct("Atomic Habits", "Self improvement book", new BigDecimal("29.99"), 40, books),
		        createProduct("Rich Dad Poor Dad", "Financial literacy book", new BigDecimal("19.99"), 50, books),
		        createProduct("The Alchemist", "Inspirational novel", new BigDecimal("14.99"), 45, books),
		        createProduct("Harry Potter", "Fantasy novel series", new BigDecimal("49.99"), 25, books),
		        createProduct("Spring in Action", "Spring Boot guide", new BigDecimal("44.99"), 20, books),
		        createProduct("Java Programming", "Core Java concepts", new BigDecimal("34.99"), 35, books),
		        createProduct("Data Structures", "CS fundamentals", new BigDecimal("24.99"), 30, books),
		        createProduct("Deep Work", "Focus and productivity", new BigDecimal("18.99"), 28, books),
		        createProduct("Zero to One", "Startup thinking book", new BigDecimal("22.99"), 32, books)

		);
		
		prodRepo.saveAll(products);		
		
	}
	
	private Product createProduct(String name,
            String desc,
            BigDecimal price,
            int stock,
            Category category) {

		Product p = new Product();
		p.setProductName(name);
		p.setDescription(desc);
		p.setPrice(price);
		p.setStockQuantity(stock);
		p.setActive(true);
		p.setCategory(category);
		
		return p;
	}
}
