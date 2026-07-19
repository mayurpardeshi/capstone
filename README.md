### 🛒 **DesiCart | Enterprise E-Commerce Backend**
DesiCart is a robust, highly-scalable E-commerce platform built with Spring Boot. 
It features a modern microservices-ready architecture, integrating real-time event streaming, distributed caching, and secure payment processing.

## 🚀 **Key Technical Features**
#### 🛡️ Security & Authentication
*   **OAuth2 + JWT**: Secure stateless authentication implemented via Spring Security using dynamically generated asymmetric RSA key pairs.
*   **Role-Based Access Control (RBAC)**: Fine-grained permissions for Users (`ROLE_USER`) and Admins (`ROLE_ADMIN`).

#### 💳 Payment & Webhooks
*   **Razorpay Integration**: Seamless checkout experience with secure payment gateway integration.
*   **Webhook Handling**: Robust webhook listeners to handle asynchronous payment success/failure notifications and order state updates.

#### ⚡ Performance & Scalability
*   **Event-Driven (Kafka)**: Integrated Kafka topics for decoupled notification communication and high-throughput event processing.
*   **Distributed Caching (Redis)**: Redis integration for caching frequent product queries, category listings, and improving application latency.
*   **Concurrency Control**: Strict use of `@Transactional` and Hibernate optimization to ensure data integrity during high-traffic checkout events.

#### 🏗️ Engineering Excellence
*   **SOLID Principles**: Clean-code codebase designed for extensibility and maintainability.
*   **Robust Logging**: Detailed application logs for monitoring and production debugging.
*   **Observability**: Integrated Spring Boot Actuators for health checks and system metrics.
*   **API Documentation**: Interactive documentation using Swagger UI (SpringDoc OpenAPI).

### **Tech Stack**
*   **Backend**: Java 21, Spring Boot 3.3.5, Spring Security (OAuth2/JWT)
*   **Data**: MySQL, Hibernate (JPA), Redis (Lettuce)
*   **Messaging**: Apache Kafka
*   **Payments**: Razorpay API & Webhooks
*   **Testing & Mocking**: Data seeders with Java-Faker (`net.datafaker`)
*   **DevOps**: Docker, Docker-Compose (Multi-container orchestration)

### 📦 Project Structure & Workflow
1.  **Auth**: User registers/logs in via OAuth2/JWT.
2.  **Catalog**: Browse products seeded via Faker API.
3.  **Cart & Order**: Add items to cart and place orders using transactional logic.
4.  **Payment**: Initiate Razorpay payment; status is updated via secure Webhooks.
5.  **Events**: Kafka producers trigger notifications/inventory updates upon order completion.

## 🚦 Getting Started
**Prerequisites**
*   Docker & Docker Compose
*   JDK 21
*   Maven

**Installation & Setup**
1.  Clone the repository:
    ```bash
    git clone https://github.com/mayurpardeshi/capstone.git
    cd capstone
    ```
2.  Spin up Infrastructure (Kafka, Redis, MySQL):
    ```bash
    docker-compose up -d
    ```
3.  Configure Environment: Update `src/main/resources/application.properties` (or `application-dev.properties`) with your Razorpay API keys and database credentials.
4.  Run the Application:
    ```bash
    mvn spring-boot:run
    ```

### API
*   **Access API Documentation**: Open `http://localhost:8081/swagger-ui.html` to explore the endpoints.
*   The project includes a built-in **Data Seeder** component. Upon startup (when using the dev profile), it uses Java-Faker to populate the MySQL database with mock Users, Categories, and a realistic Product Catalog.

#### 🐳 Docker Orchestration
The project includes a `docker-compose.yml` that manages:
*   **Redis**: For high-speed data retrieval.
*   **Kafka & Zookeeper**: For the event-driven backbone.
*   **MySQL Instance**: The primary persistent store.

#### 📝 License
This project is licensed under the MIT License.

Developed by **Mayur Pardeshi**
