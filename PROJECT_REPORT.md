# DesiCart Project Report

## 1. Project Overview
DesiCart is a comprehensive e-commerce platform built using the Spring Boot framework. It provides a robust set of features for managing products, categories, shopping carts, orders, and user authentication. The project follows a modern, scalable architecture, integrating various technologies to ensure performance and reliability.

## 2. Technical Stack
- **Language**: Java 21 / 17
- **Framework**: Spring Boot 3.3.5
- **Data Access**: Spring Data JPA, Hibernate
- **Database**: MySQL (Production), H2 (Testing)
- **Security**: Spring Security, JWT (JSON Web Token), OAuth2 Resource Server
- **Messaging**: Spring Kafka (for event-driven notifications)
- **Caching**: Spring Data Redis (for performance and rate limiting)
- **Payment Integration**: Razorpay, PhonePe
- **API Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Utilities**: Lombok, MapStruct, DataFaker

## 3. Core Modules and Features

### 3.1. User Management
- Secure user registration and authentication using JWT.
- Role-based access control (RBAC) with support for multiple roles (e.g., ADMIN, USER).
- Password management (reset/update functionality).

### 3.2. Product Catalog
- Management of products with attributes like name, brand, price, inventory, and description.
- Product categorization for easier navigation.
- Image management for products.
- Product seeding for development and testing using DataFaker.

### 3.3. Shopping Cart
- User-specific shopping carts.
- Add, update, and remove items from the cart.
- Automatic subtotal and total amount calculation.
- Support for soft delete for cart items.

### 3.4. Order & Checkout Processing
- Transition from shopping cart to order.
- Support for multiple payment gateways (Razorpay, PhonePe).
- Order tracking with various statuses (PENDING, PAID, SHIPPED, etc.).
- Webhook integration for real-time payment status updates.

### 3.5. Notifications & Messaging
- Event-driven architecture using Kafka for sending notifications.
- Decoupled notification service from core business logic.

### 3.6. Performance & Scalability
- Redis integration for caching and rate limiting to protect the API.
- Layered architecture ensuring separation of concerns and maintainability.

## 4. Architectural Design
The project follows a standard N-tier architecture:
1.  **Controller Layer**: Handles HTTP requests and maps them to service methods.
2.  **Service Layer**: Contains business logic and orchestrates data flow.
3.  **Repository Layer**: Manages data persistence using Spring Data JPA.
4.  **Model/DTO Layer**: Defines entities and data transfer objects for data exchange.

## 5. Conclusion
DesiCart is a well-structured e-commerce solution that leverages the Spring ecosystem to provide a secure, scalable, and feature-rich shopping experience. Its integration with modern technologies like Kafka, Redis, and various payment gateways makes it suitable for real-world applications.
