# SpringCart

A production-ready e-commerce backend platform built with Spring Boot, featuring JWT authentication, complex JPA relationships, and transactional workflows for secure and scalable online commerce operations.

---

## Description

SpringCart is a full-featured RESTful e-commerce API that manages the complete customer lifecycle from product browsing to order fulfillment. The platform provides secure user authentication, real-time inventory management, and transactional order processing with automatic stock control.

Built with Spring Boot and Spring Security, the application features JWT-based stateless authentication, role-based authorization for customer and administrative operations, and comprehensive error handling. The backend handles complex entity relationships across seven database tables, processes paginated product catalogs, and ensures data consistency through transactional workflows during order placement and cancellation.

The system supports multiple concurrent users, maintains price snapshots for order integrity, and implements soft-delete patterns for data preservation. All endpoints follow RESTful principles with proper HTTP methods, status codes, and resource-oriented URL structures.

---

## Features

### Core Functionality
- Category and Product Management with hierarchical relationships
- User registration and authentication with JWT tokens
- Shopping cart with real-time total calculation and stock validation
- Order management with transactional workflows and status tracking
- Advanced product search with case-insensitive matching
- Pagination and sorting with customizable parameters
- Stock management with automatic deduction and restoration

### Security
- JWT-based stateless authentication (1-hour token expiration)
- BCrypt password encryption
- Role-based access control (CUSTOMER and ADMIN roles)
- Protected endpoints with fine-grained authorization
- Secure password storage and validation

### Technical Implementation
- DTO pattern for clean API contracts
- Global exception handling with standardized error responses
- Bean Validation for comprehensive input validation
- Transaction management ensuring ACID compliance
- Custom ID generation (sequential for users, UUID for orders)
- Soft delete for user deactivation
- Price snapshots for historical accuracy in orders

---

## Tech Stack

**Backend Framework**
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- Spring Web

**Database & ORM**
- H2 Database (Development)
- Hibernate ORM

**Security**
- JSON Web Tokens (JWT)
- BCrypt Password Encryption

**Build & Tools**
- Java 17
- Maven
- Lombok
- Jakarta Bean Validation

---

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Installation

1. Clone the repository
```bash
git clone https://github.com/SoumyaGanesh12/SpringCart.git
cd SpringCart
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`

### H2 Database Console (Optional)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/springcart`
- Username: `sa`
- Password: (leave empty)

### Configuration
Update `application.properties` for production with MySQL/PostgreSQL:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springcart
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

---

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token

### Categories
- `GET /api/public/categories` - Get all categories
- `POST /api/public/categories` - Create category
- `PUT /api/public/categories/{id}` - Update category
- `DELETE /api/admin/categories/{id}` - Delete category (Admin only)

### Products
- `GET /api/public/products` - Get all products
- `GET /api/public/products/{id}` - Get product by ID
- `GET /api/public/products/search?keyword={keyword}` - Search products
- `GET /api/public/categories/{categoryId}/products` - Get products by category
- `GET /api/public/products/page?page={page}&size={size}&sortBy={field}&sortDir={direction}` - Paginated products
- `POST /api/admin/products` - Create product (Admin only)
- `PUT /api/admin/products/{id}` - Update product (Admin only)
- `DELETE /api/admin/products/{id}` - Delete product (Admin only)

### Users
- `GET /api/admin/users` - Get all users (Admin only)
- `GET /api/public/users/{userId}` - Get user profile
- `PUT /api/users/profile` - Update current user's profile
- `DELETE /api/admin/users/{userId}` - Deactivate user (Admin only)
- `PATCH /api/admin/users/{userId}/toggle-status` - Toggle user status (Admin only)

### Shopping Cart
- `GET /api/cart` - Get current user's cart
- `POST /api/cart/add` - Add product to cart
- `PUT /api/cart/items/{itemId}` - Update item quantity
- `DELETE /api/cart/items/{itemId}` - Remove item from cart
- `DELETE /api/cart` - Clear cart

### Orders
- `POST /api/orders/place` - Place order from cart
- `GET /api/orders/{orderId}` - Get order by ID
- `GET /api/orders` - Get current user's order history
- `PATCH /api/orders/{orderId}/cancel` - Cancel order
- `GET /api/admin/orders` - Get all orders (Admin only)
- `GET /api/admin/orders/page?page={page}&size={size}` - Paginated orders (Admin only)
- `PATCH /api/admin/orders/{orderId}/status` - Update order status (Admin only)

---

## Project Structure

```
src/main/java/com/ecommerce/project/
├── controller/        # REST API endpoints (6 controllers)
├── dto/               # Data Transfer Objects (16 DTOs)
├── exception/         # Custom exceptions and global handler
├── model/             # JPA entities (7 entities)
├── repository/        # Data access layer (7 repositories)
├── security/          # JWT and Spring Security configuration
└── service/           # Business logic layer (8 services)
```

---

## Key Technical Implementations

### Authentication & Security
- JWT-based stateless authentication with 1-hour token expiration
- BCrypt password hashing with salt for secure storage
- Role-based access control separating CUSTOMER and ADMIN operations
- Custom JWT filter validating tokens on every request

### Database & JPA
- Complex entity relationships: OneToOne (User-Cart), OneToMany (Category-Products, Cart-CartItems, Order-OrderItems), ManyToOne (Product-Category)
- Lazy loading fetch strategies for optimized performance
- Custom repository query methods for search and filtering
- Pagination using Spring Data Pageable interface

### Business Logic
- Transactional order placement ensuring atomicity across order creation, stock deduction, and cart clearing
- Stock management with real-time validation and automatic restoration on order cancellation
- Price snapshots preserving historical pricing in cart and order items
- Order lifecycle management with status transitions and business rule enforcement

### API Design
- DTO pattern separating API contracts from database entities
- Global exception handling with @RestControllerAdvice
- Bean Validation for declarative input constraints
- RESTful design with proper HTTP methods and status codes
- User identity extraction from JWT tokens for secure operations

---

## Testing

Comprehensive unit test suite for the service layer using JUnit 5 and Mockito, achieving high code coverage across business logic components.

### Test Statistics
- **Total Tests:** 56 unit tests
- **Service Layer Coverage:** 87%
- **Overall Project Coverage:** 60%
- **Testing Framework:** JUnit 5
- **Mocking Framework:** Mockito

### Test Coverage by Service
- **CategoryServiceImpl:** 6 tests - CRUD operations, duplicate validation, error scenarios
- **ProductServiceImpl:** 13 tests - Product management, pagination, search, category relationships
- **UserServiceImpl:** 9 tests - Registration, authentication, profile management, soft delete
- **CartServiceImpl:** 12 tests - Cart operations, stock validation, duplicate product handling
- **OrderServiceImpl:** 16 tests - Order placement, cancellation, stock restoration, permission checks

### Testing Approach
- Service layer tested in isolation with mocked repository dependencies
- Arrange-Act-Assert pattern for clear and maintainable test structure
- Comprehensive coverage of happy paths and error scenarios
- Business rule validation including stock management and order lifecycle
- Transaction behavior and data integrity verification

### Running Tests
```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn clean test

# View detailed coverage report
open target/site/jacoco/index.html
```

### Code Coverage Report
JaCoCo generates detailed HTML reports showing:
- Line and branch coverage by package and class
- Visual code highlighting (green: covered, red: not covered)
- Method-level coverage metrics
- Overall project statistics

---

## License

This project is licensed under the MIT License.
