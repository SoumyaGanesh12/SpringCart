# SpringCart

A production-ready e-commerce backend platform built with Spring Boot, featuring JSON Web Token (JWT) authentication, Redis caching, Stripe payment processing, and transactional order workflows.

---

## Features

- **Authentication & Authorization** — JSON Web Token (JWT) based stateless authentication with 1-hour token expiration and role-based access control separating CUSTOMER and ADMIN operations
- **Product Catalog** — Full product and category management with case-insensitive search, pagination, and sorting with customizable parameters
- **Shopping Cart** — Real-time cart management with stock validation, price snapshots, and automatic total calculation
- **Order Management** — Transactional order placement with automatic stock deduction, order lifecycle tracking, and stock restoration on cancellation
- **Payment Processing** — Stripe PaymentIntent integration with webhook-based payment confirmation and automatic refunds on order cancellation
- **Caching** — Redis caching for frequently accessed product and category data with automatic cache invalidation on updates
- **Security** — BCrypt password encryption, protected endpoints with fine-grained authorization, and Stripe webhook signature verification
- **Data Integrity** — ACID-compliant transactions, price snapshots for historical accuracy, soft delete for user deactivation, and custom ID generation (sequential for users, UUID for orders)
- **API Design** — RESTful endpoints with Data Transfer Object (DTO) pattern, global exception handling, and Jakarta Bean Validation for input constraints

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.x, Spring Web, Spring Security, Spring Data Java Persistence API (JPA) |
| Object Relational Mapping (ORM) | Hibernate ORM |
| Database | MySQL 8.x (production), H2 (testing) |
| Caching | Redis (Memurai for Windows) |
| Security | JSON Web Token (JWT), BCrypt |
| Payments | Stripe API |
| Containerization | Docker, Docker Compose |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito, JaCoCo |
| Build Tools | Java 17, Maven, Lombok |

---

## Complete Application Flow

```
                        CLIENT REQUEST
                              │
                              ▼
               ┌──────────────────────────┐
               │  Spring Security Filter   │
               │  Validate JWT Token       │
               │  Check Role Permissions   │
               └──────────────────────────┘
                              │
                              ▼
               ┌──────────────────────────┐
               │     REST Controllers      │
               │  Auth / Products / Cart   │
               │  Orders / Payments        │
               └──────────────────────────┘
                              │
                              ▼
               ┌──────────────────────────┐
               │      Service Layer        │
               │    Business Logic +       │
               │  Transaction Management   │
               └──────────────────────────┘
                    │              │
          ┌─────────┘              └──────────┐
          ▼                                   ▼
┌──────────────────┐              ┌───────────────────┐
│   Redis Cache     │              │   MySQL Database   │
│  Categories       │              │  Users / Products  │
│  Products         │              │  Orders / Cart     │
└──────────────────┘              └───────────────────┘
                                             │
                        ┌────────────────────┘
                        ▼
           ┌─────────────────────┐
           │     Stripe API       │
           │  Create Payment      │
           │  Intent (pi_xxx)     │
           └─────────────────────┘
                        │
                        ▼
           ┌─────────────────────┐
           │  Payment Confirmed   │
           │  Stripe fires        │
           │  Webhook Event       │
           └─────────────────────┘
                        │
                        ▼
           ┌─────────────────────┐
           │  Webhook Controller  │
           │  Verify Signature    │
           │  Order → CONFIRMED   │
           └─────────────────────┘

─────────────────────────────────────────────────────
ORDER LIFECYCLE:
PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED
  │
  └── Cancel (before SHIPPED) → Stripe Refund + Stock Restored
```

---

## Entity Relationships

```
Category  (1) ──► (Many) Product
User      (1) ──► (1)    Cart
User      (1) ──► (Many) Order
Cart      (1) ──► (Many) CartItem  ──► (1) Product
Order     (1) ──► (Many) OrderItem ──► (1) Product
```

---

## API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register new user |
| POST | `/api/auth/login` | Public | Login and receive JWT token |

### Categories
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/public/categories` | Public | Get all categories |
| POST | `/api/public/categories` | Public | Create category |
| PUT | `/api/public/categories/{id}` | Public | Update category |
| DELETE | `/api/admin/categories/{id}` | Admin | Delete category |

### Products
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/public/products` | Public | Get all products |
| GET | `/api/public/products/{id}` | Public | Get product by ID |
| GET | `/api/public/products/search?keyword={keyword}` | Public | Search products |
| GET | `/api/public/categories/{categoryId}/products` | Public | Products by category |
| GET | `/api/public/products/page` | Public | Paginated products |
| POST | `/api/admin/products` | Admin | Create product |
| PUT | `/api/admin/products/{id}` | Admin | Update product |
| DELETE | `/api/admin/products/{id}` | Admin | Delete product |

### Users
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/admin/users` | Admin | Get all users |
| GET | `/api/public/users/{userId}` | Public | Get user profile |
| PUT | `/api/users/profile` | User | Update profile |
| DELETE | `/api/admin/users/{userId}` | Admin | Deactivate user (soft delete) |
| PATCH | `/api/admin/users/{userId}/toggle-status` | Admin | Toggle user status |

### Shopping Cart
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/cart` | User | Get cart |
| POST | `/api/cart/add` | User | Add product to cart |
| PUT | `/api/cart/items/{itemId}` | User | Update item quantity |
| DELETE | `/api/cart/items/{itemId}` | User | Remove item |
| DELETE | `/api/cart` | User | Clear cart |

### Orders
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/users/{userId}/orders/place` | User | Place order from cart |
| GET | `/api/orders/{orderId}` | User | Get order by ID |
| GET | `/api/orders` | User | Get order history |
| PATCH | `/api/orders/{orderId}/cancel` | User/Admin | Cancel order |
| GET | `/api/admin/orders` | Admin | Get all orders |
| PATCH | `/api/admin/orders/{orderId}/status` | Admin | Update order status |

### Payments
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/payments/create-intent` | User | Create Stripe PaymentIntent |
| POST | `/api/webhooks/stripe` | Stripe | Receive webhook events |

---

## Running the Application

### Option 1: Docker

**Prerequisites:** Docker Desktop

1. Clone the repository
```bash
git clone https://github.com/SoumyaGanesh12/SpringCart.git
cd SpringCart
```

2. Create `.env` from the example
```bash
cp .env.example .env
```
Fill in your values in `.env`

3. Start all services
```bash
docker-compose up --build
```

App runs at `http://localhost:8080`
MySQL on port `3307` · Redis on port `6380`

---

### Option 2: Local Setup

**Prerequisites:** Java 17, Maven, MySQL 8.x, Redis

**MySQL Setup:**
```sql
CREATE DATABASE springcart;
CREATE USER 'springcart_user'@'localhost' IDENTIFIED BY 'springcart123';
GRANT ALL PRIVILEGES ON springcart.* TO 'springcart_user'@'localhost';
FLUSH PRIVILEGES;
```

**Redis Setup (Windows):**
1. Download Memurai from https://www.memurai.com/get-memurai
2. Install — runs automatically as a Windows service
3. Verify: `memurai-cli ping` → should return `PONG`

**Run the application:**
1. Clone the repository
```bash
git clone https://github.com/SoumyaGanesh12/SpringCart.git
cd SpringCart
```

2. Create `src/main/resources/application-dev.properties` from `application-dev.properties.example` and fill in your values

3. Start the app
```bash
mvn spring-boot:run
```

---

## Stripe Payment Testing

1. Create a Stripe account at https://stripe.com and get your test API keys

2. Install Stripe CLI from https://stripe.com/docs/stripe-cli

3. Login
```bash
stripe login
```

4. Start webhook forwarding — keep this running
```bash
stripe listen --forward-to localhost:8080/api/webhooks/stripe
```
Copy the `whsec_xxx` secret to your config

5. Confirm a payment intent to simulate payment
```bash
stripe payment_intents confirm pi_xxx --payment-method=pm_card_visa
```

> All Stripe testing uses test mode — no real charges are made

---

## Caching Strategy

Redis caching reduces database load for frequently accessed data:

| Cache | Method | Invalidated On |
|-------|--------|---------------|
| `allCategories` | `getAllCategories()` | Create / Update / Delete |
| `categories` | `getCategoryById()` | Update / Delete |
| `allProducts` | `getAllProducts()` | Create / Update / Delete |
| `products` | `getProductById()` | Update / Delete |
| `productsByCategory` | `getProductsByCategory()` | Update / Delete |

**Not cached by design:** Paginated results, search results, cart data, and stock quantities — these require real-time accuracy or have too many key variations.

---

## Testing

| Metric | Value |
|--------|-------|
| Total Tests | 56 unit tests |
| Service Layer Coverage | 87% |
| Overall Coverage | 60% |
| Framework | JUnit 5 + Mockito |
| Coverage Reports | JaCoCo |

```bash
# Run all tests
mvn test

# Run with JaCoCo coverage report
mvn clean test

# View detailed HTML coverage report
open target/site/jacoco/index.html
```

---

## Project Structure

```
src/main/java/com/ecommerce/project/
├── config/        # Redis, Stripe configuration
├── controller/    # REST controllers (7 controllers)
├── dto/           # Data Transfer Objects
├── exception/     # Custom exceptions and global handler
├── model/         # JPA entities (7 entities)
├── repository/    # Data access layer
├── security/      # JWT filter, Spring Security config
└── service/       # Business logic layer
```