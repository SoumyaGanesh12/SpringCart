# SpringCart

A RESTful e-commerce backend application built with Spring Boot.

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Hibernate**
- **H2 Database**
- **Maven**
- **Lombok**

## Features

- Category Management (CRUD operations)
- Product Management with category relationships
- Search and filter products
- Pagination and sorting
- Global exception handling
- DTO pattern implementation

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository
```bash
git clone https://github.com/SoumyaGanesh12/SpringCart.git
cd SpringCart
```

2. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Categories
- `GET /api/public/categories` - Get all categories
- `POST /api/public/categories` - Create category
- `PUT /api/public/categories/{id}` - Update category
- `DELETE /api/admin/categories/{id}` - Delete category

### Products
- `GET /api/public/products` - Get all products
- `GET /api/public/products/{id}` - Get product by ID
- `POST /api/admin/products` - Create product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Delete product
- `GET /api/public/products/search?keyword={keyword}` - Search products
- `GET /api/public/products/page?page=0&size=10` - Get products with pagination

## Project Structure

```
src/main/java/com/ecommerce/project/
├── controller/     # REST Controllers
├── dto/            # Data Transfer Objects
├── exception/      # Exception handling
├── model/          # JPA Entities
├── repository/     # Data repositories
└── service/        # Business logic
```

## Status

**Work in Progress** - This project is under active development.

## License

This project is licensed under the MIT License.