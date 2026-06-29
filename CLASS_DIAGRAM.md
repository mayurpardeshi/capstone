# DesiCart Class Diagram

This diagram represents the core entity relationships within the DesiCart project.

```mermaid
classDiagram
    class BaseModel {
        <<abstract>>
        +Long id
        +boolean deleted
        +LocalDateTime createdAt
        +LocalDateTime updatedAt
    }

    class User {
        +Long id
        +String firstName
        +String lastName
        +String userId
        +String password
        +Set~Role~ roles
    }

    class Role {
        +Long id
        +String name
    }

    class Product {
        +String name
        +String brand
        +BigDecimal price
        +Integer inventory
        +String description
        +Category category
        +List~Image~ images
    }

    class Category {
        +String name
        +String description
        +List~Product~ products
    }

    class Image {
        +String url
        +Product product
    }

    class Cart {
        +BigDecimal totalAmount
        +User user
        +Set~CartItem~ cartItems
        +addItem(CartItem)
        +removeItem(CartItem)
        +updateTotalAmount()
    }

    class CartItem {
        +int quantity
        +BigDecimal unitPrice
        +BigDecimal totalPrice
        +Product product
        +Cart cart
    }

    class Order {
        +LocalDateTime orderDate
        +BigDecimal totalAmount
        +OrderStatus orderStatus
        +String gatewayOrderId
        +String gatewayName
        +Set~OrderItem~ orderItems
        +User user
        +PaymentDetails paymentDetails
    }

    class OrderItem {
        +int quantity
        +BigDecimal price
        +String name
        +String brand
        +String description
        +Order order
        +Product product
    }

    class PaymentDetails {
        +String gatewayOrderId
        +String gatewayPaymentId
        +String gatewayName
        +String status
        +LocalDateTime paymentDate
        +BigDecimal amount
        +Order order
    }

    BaseModel <|-- Product
    BaseModel <|-- Category
    BaseModel <|-- Image
    BaseModel <|-- Cart
    BaseModel <|-- CartItem
    BaseModel <|-- Order
    BaseModel <|-- OrderItem
    BaseModel <|-- PaymentDetails

    User "*" o-- "*" Role : users_roles
    User "1" -- "1" Cart : user_id
    User "1" -- "*" Order : user_id

    Product "*" -- "1" Category : category_id
    Product "1" -- "*" Image : product_id

    Cart "1" -- "*" CartItem : cart_id
    CartItem "*" -- "1" Product : product_id

    Order "1" -- "*" OrderItem : order_id
    Order "1" -- "1" PaymentDetails : order_id
    OrderItem "*" -- "1" Product : product_id
```

## Enums

### OrderStatus
- ORDER_PLACED
- ORDER_PENDING
- ORDER_CHECKED_OUT
- ORDER_EXPIRED
- ORDER_PAYMENT_INITIATED
- ORDER_PAID
- ORDER_PAYMENT_FAILED
- ORDER_PAYMENT_CANCELLED
- ORDER_SHIPPED
- ORDER_DELIVERED

### PaymentStatus
- SUCCESS
- FAILURE
- INITIATED
- CREATED
