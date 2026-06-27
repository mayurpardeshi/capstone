package com.mayur.DesiCart.shop.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentWebhookDto;
import com.mayur.DesiCart.shop.cartAndCheckout.exceptions.StockInsufficientException;
import com.mayur.DesiCart.shop.cartAndCheckout.models.Cart;
import com.mayur.DesiCart.shop.cartAndCheckout.services.CartService;
import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentProcessor;
import com.mayur.DesiCart.shop.cartAndCheckout.services.PaymentService;
import com.mayur.DesiCart.shop.notifications.events.PaymentNotificationEvent;
import com.mayur.DesiCart.shop.notifications.service.NotificationProducer;
import com.mayur.DesiCart.shop.order.dto.OrderDto;
import com.mayur.DesiCart.shop.order.dto.OrderItemDto;
import com.mayur.DesiCart.shop.order.mapper.OrderMapper;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderItem;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.models.PaymentDetails;
import com.mayur.DesiCart.shop.order.orderRepository.OrderRepository;
import com.mayur.DesiCart.shop.order.orderRepository.PaymentDetailsRepository;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.product.models.Product;
import com.mayur.DesiCart.shop.product.repositories.ProductRepository;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.service.UserService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final CartService cartService;
    private final UserService userService;
    private final PaymentDetailsRepository paymentDetailRepository;
    private final OrderMapper orderMapper;
    private final PaymentService paymentService;
    private final NotificationProducer notificationProducer;

    /*
    * Step 1 of User payment journey
    * Moves Cart-items from a Cart to Order-items of an Order
    * Find existing ORDER_PLACED or create a new one
    * */
    @Override
    @Transactional // Ensures Atomicity
    public OrderDto placeOrder() {
        // 1. Get the current user and their active cart
        User user = userService.getAuthenticatedUser();
        Cart cart = cartService.getCart();

        boolean isAlreadyPlacedOrder = orderRepository.existsByUserAndOrderStatus(user, OrderStatus.ORDER_PLACED);
        if (isAlreadyPlacedOrder){
            Order existingOrder = orderRepository.findTopByUserAndOrderStatusInOrderByCreatedAtDesc(user, List.of(OrderStatus.ORDER_PLACED))
                    .stream()
                    .findFirst()
                    .orElse(null);
            throw new IllegalStateException(String.format(
                    "Order is already placed for user %s with cart_id %d. Current Order ID: %d",
                    user.getUserId(), cart.getId(), (existingOrder != null ? existingOrder.getId() : 0)
            ));
        }

        // 2. Initialize Order
        // checking orders based on timestamp - findTopByUserAndOrderStatusInOrderByCreatedTimeDesc handle concurrency
        Order order = orderRepository.findTopByUserAndOrderStatusInOrderByCreatedAtDesc(user,
                        List.of(OrderStatus.ORDER_PENDING, OrderStatus.ORDER_PAYMENT_FAILED, OrderStatus.ORDER_PAYMENT_CANCELLED)).stream().findFirst()
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUser(user);
                    return newOrder;
                });
        order.setUser(user);
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderStatus(OrderStatus.ORDER_PLACED);
        order.setOrderDate(LocalDateTime.now());

        //Transform CartItems to OrderItems and link them
        Set<OrderItem> orderItems = cart.getCartItems().stream()
                .filter(cartItem -> !cartItem.isDeleted())
                        .map(cartItem -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setProduct(cartItem.getProduct());
                            orderItem.setQuantity(cartItem.getQuantity());
                            orderItem.setPrice(cartItem.getUnitPrice());
                            orderItem.setName(cartItem.getProduct().getName());
                            orderItem.setBrand(cartItem.getProduct().getBrand());
                            orderItem.setDescription(cartItem.getProduct().getDescription());
                            orderItem.setOrder(order);
                            return orderItem;
                        }).collect(Collectors.toSet());

        // update Order State
        order.setOrderItems(orderItems);
        order.setTotalAmount(calculateTotalAmount(orderItems));

        // Save (order items are saved via CascadingType.ALL)
        Order saveOrder = orderRepository.save(order);
        // DO NOT clear the cart

        //Now send to kafka
        if (order.getOrderStatus() != null){
            PaymentNotificationEvent event = new PaymentNotificationEvent(
                    order.getId(),
                    order.getUser().getUserId(),
                    order.getUser().getFirstName(),
                    order.getTotalAmount(),
                    order.getOrderStatus().name()
            );
            notificationProducer.sendNotification(event);

        }

        return orderMapper.toDto(saveOrder);
    }


    private BigDecimal calculateTotalAmount(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getItemTotal(OrderItem item) {
        if (item.getPrice() == null) return BigDecimal.ZERO;
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("No orders found with orderId: "+orderId));
    }

    @Override
    public List<OrderDto> getOrderByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setTotalAmount(order.getTotalAmount());
        orderDto.setStatus(order.getOrderStatus().toString());

        List<OrderItemDto> itemDtos = order.getOrderItems().stream().map(item -> {
            OrderItemDto itemDto = new OrderItemDto();
            itemDto.setQuantity(item.getQuantity());
            itemDto.setPrice(item.getPrice());

            if (item.getProduct() != null) {
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setProductBrand(item.getProduct().getBrand());
            }
            return itemDto;
        }).toList();

        orderDto.setOrderItems(itemDtos);
        return orderDto;
    }

    /**
     * --------------------------------------------------------------------
     * COMPLETE ORDER AFTER PAYMENT CAPTURE (WEBHOOK FLOW)
     * --------------------------------------------------------------------
     * Processes a successful payment webhook and completes the order.
     *
     * This method:
     *  - Validates the incoming webhook payload
     *  - Fetches the corresponding Order using the gateway order ID
     *  - Ensures idempotent processing (no double capture)
     *  - Updates payment details with gateway metadata
     *  - Marks the order as PAID
     *  - Reduces product inventory
     *  - Clears the user's cart
     *
     * Transactional guarantees:
     *  - All DB changes are committed atomically
     *  - On any failure, inventory/order/payment updates are rolled back
     *
     * Input:
     *  - dto (PaymentWebhookDto):
     *      - gatewayOrderId     : String (required)
     *      - gatewayPaymentId  : String
     *      - amount            : Long (in paise)
     *      - email             : String
     *      - contact           : String
     *      - method            : String (card, upi, wallet, etc.)
     *      - wallet            : String
     *      - bank              : String
     *      - tax               : BigDecimal
     *      - fee               : BigDecimal
     *
     * Output:
     *  - void
     *
     * Throws:
     *  - ResourceNotFoundException if order metadata is missing
     *  - RuntimeException for unexpected gateway or persistence failures
     * --------------------------------------------------------------------
     */
    @Transactional
    public void completeOrder(PaymentWebhookDto dto) {

        log.info("Completing order for Gateway Order ID: {}", dto.getGatewayOrderId());

        /* ------------------------------------------------------------
         * 1. Fetch Order using Gateway Order ID
         * ------------------------------------------------------------ */
        Order order = orderRepository.findByGatewayOrderId(dto.getGatewayOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found for gatewayOrderId="
                                + dto.getGatewayOrderId()));

        /* ------------------------------------------------------------
         * 2. Fetch or Reconstruct Payment Details (Gateway Fallback)
         * ------------------------------------------------------------ */
        PaymentDetails payment = paymentDetailRepository
                .findByGatewayOrderId(dto.getGatewayOrderId())
                .orElseGet(() -> {
                    log.warn("Payment not found in DB. Triggering gateway verification. Gateway={}",
                            order.getGatewayName());
                    PaymentProcessor processor =
                            paymentService.paymentProcessorMap.get(order.getGatewayName());
                    return processor.verifyPayment(dto.getGatewayOrderId());
                });

        /* ------------------------------------------------------------
         * 3. IDEMPOTENCY CHECK
         * ------------------------------------------------------------ */
        if ("CAPTURED".equals(payment.getStatus())) {
            log.warn("Payment already captured for gatewayOrderId={}. Skipping processing.",
                    dto.getGatewayOrderId());
            return;
        }

        /* ------------------------------------------------------------
         * 4. Map Gateway Payload → Payment Entity
         * ------------------------------------------------------------ */
        payment.setGatewayPaymentId(dto.getGatewayPaymentId());
        payment.setEmail(dto.getEmail());
        payment.setContact(dto.getContact());
        payment.setMethod(dto.getMethod());
        payment.setWallet(dto.getWallet());
        payment.setBank(dto.getBank());

        if (dto.getAmount() != null) {
            BigDecimal amountInBaseCurrency = BigDecimal.valueOf(dto.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            payment.setAmount(amountInBaseCurrency);
        }

        payment.setTax(dto.getTax());
        payment.setFee(dto.getFee());
        payment.setStatus(OrderStatus.ORDER_PAID.name());

        /* ------------------------------------------------------------
         * 5. Update Order Status & Inventory
         * ------------------------------------------------------------ */
        order.setOrderStatus(OrderStatus.ORDER_PAID);
        reduceInventory(order.getOrderItems());

        /* ------------------------------------------------------------
         * 6. Persist Changes
         * ------------------------------------------------------------ */
        paymentDetailRepository.save(payment);
        orderRepository.save(order);

        /* ------------------------------------------------------------
         * 7. Post-Order Cleanup
         * ------------------------------------------------------------ */
        cartService.clearCart(order.getUser().getId());
        PaymentNotificationEvent event = new PaymentNotificationEvent(
                order.getId(),
                order.getUser().getUserId(),
                order.getUser().getFirstName(),
                order.getTotalAmount(),
                "PAID"
        );
        notificationProducer.sendNotification(event);
    }


    /**
     * --------------------------------------------------------------------
     * REDUCE PRODUCT INVENTORY AFTER SUCCESSFUL ORDER
     * --------------------------------------------------------------------
     * Deducts inventory quantities for all valid (non-deleted) order items from the user's cart
     *
     * This method:
     *  - Iterates through order items associated with a paid order
     *  - Validates available inventory for each product
     *  - Reduces stock based on quantity sold
     *  - Persists updated inventory state
     *
     * Transactional behavior:
     *  - Executes within an existing transaction
     *  - Any inventory shortage will cause rollback of the entire order flow
     *
     * Input:
     *  - items (Set<OrderItem>)
     *      - product     : Product (must not be null)
     *      - quantity    : int (quantity sold)
     *      - deleted     : boolean (soft delete flag)
     *
     * Output:
     *  - void
     *
     * Throws:
     *  - IllegalStateException if inventory is insufficient
     * --------------------------------------------------------------------
     */
    @Transactional
    public void reduceInventory(Set<OrderItem> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        items.stream()
                .filter(item -> !item.isDeleted())
                .forEach(orderItem -> {

                    /* ------------------------------------------------------------
                     * 1. Validate Product Reference
                     * ------------------------------------------------------------ */
                    Product product = orderItem.getProduct();
                    if (product == null) {
                        throw new IllegalStateException("OrderItem has no associated product");
                    }

                    /* ------------------------------------------------------------
                     * 2. Resolve Current Inventory
                     * ------------------------------------------------------------ */
                    int currentStock = product.getInventory() != null
                            ? product.getInventory()
                            : 0;

                    int quantitySold = orderItem.getQuantity();

                    /* ------------------------------------------------------------
                     * 3. Validate Stock Availability
                     * ------------------------------------------------------------ */
                    if (currentStock < quantitySold) {
                        throw new IllegalStateException(
                                String.format(
                                        "Insufficient stock for product [%s]. Available=%d, Requested=%d",
                                        product.getName(),
                                        currentStock,
                                        quantitySold
                                )
                        );
                    }

                    /* ------------------------------------------------------------
                     * 4. Reduce Inventory & Persist
                     * ------------------------------------------------------------ */
                    product.setInventory(currentStock - quantitySold);
                    productRepository.save(product);
                });
    }


    /**
     * Processes the checkout for a specific order.
     * Performs inventory validation, price freezing, and final amount calculation
     * before transitioning the order to INITIATED status and processing payment.
     *
     * @param orderId The unique identifier of the order to be processed.
     * @return PaymentInitializationResponse containing gateway-specific payment details.
     * @throws ResourceNotFoundException if the order does not exist.
     * @throws IllegalStateException if the order is not in a valid state for checkout.
     */
    @Override
    public OrderDto checkout(Long orderId) {
        // 1. fetch the order:
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(" Order not found with ID: "+orderId));

        // 2. Validate Order Status (Only PENDING orders can be checked out)
        if (order.getOrderStatus() != OrderStatus.ORDER_PLACED){
            throw new IllegalArgumentException("To process order, Order should be on ORDER_PLACED state, but current state of order is: "+order.getOrderStatus());
        }

        // 3. Inventory Check & Price Freeze
        // Logic: Ensure products haven't sold out and prices haven't changed since adding to cart
        validateInventoryAndFreezePrices(order);
        calculateGrandTotal(order);
        order.setOrderStatus(OrderStatus.ORDER_CHECKED_OUT);
        order = orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    /**
     * Processes the checkout for a specific order.
     * Performs inventory validation, price freezing, and final amount calculation
     * before transitioning the order to INITIATED status and processing payment.
     *
     * @param orderId The unique identifier of the order to be processed.
     * @param gateway The payment gateway to be used (e.g., RAZOR_PAY, STRIPE).
     * @return PaymentInitializationResponse containing gateway-specific payment details.
     * @throws ResourceNotFoundException if the order does not exist.
     * @throws IllegalStateException if the order is not in a valid state for checkout.
     */
    @Override
    public PaymentInitializationResponse initiatePayment(Long orderId, String gateway) {
        // 1. fetch the order:
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(" Order not found with ID: "+orderId));


        order.setOrderStatus(OrderStatus.ORDER_PAYMENT_INITIATED);
        orderRepository.save(order);

        // Strategy pattern
        return paymentService.processPayment(order, gateway);
    }

    @Override
    @Transactional
    public void handlePaymentSuccess(PaymentWebhookDto dto) {
        PaymentDetails paymentDetails = paymentDetailsRepository.findByGatewayOrderId(dto.getGatewayOrderId()).orElseThrow(() ->
            new IllegalStateException("Payment not found with GatewayOrderId "+ dto.getGatewayOrderId()));
        if (paymentDetails.getStatus().equals(OrderStatus.ORDER_PAID.name())){
            return; // idempotent
        }
        completeOrder(dto);
    }

    @Override
    public void handlePaymentFailure(PaymentWebhookDto dto) {
        PaymentDetails paymentDetails = paymentDetailsRepository.findByGatewayOrderId(dto.getGatewayOrderId()).orElseThrow(() ->
                new IllegalStateException("Payment not found with GatewayOrderId "+ dto.getGatewayOrderId()));
        if (paymentDetails.getStatus() == OrderStatus.ORDER_PAYMENT_FAILED.name()){
            log.info("Payment already marked as {}, skipping update. gatewayOrderId={}",
                    paymentDetails.getStatus(), dto.getGatewayOrderId());
            return;
        }
        paymentDetails.setStatus(OrderStatus.ORDER_PAYMENT_FAILED.name());
        paymentDetails.setGatewayPaymentId(dto.getGatewayPaymentId());
        paymentDetails.setEmail(dto.getEmail());
        paymentDetails.setContact(dto.getContact());
        paymentDetails.setMethod(dto.getMethod());
        paymentDetails.setWallet(dto.getWallet());
        paymentDetails.setBank(dto.getBank());
        // Amount may exist even in failure
        if (dto.getAmount() != null) {
            BigDecimal amountInBaseCurrency = BigDecimal.valueOf(dto.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            paymentDetails.setAmount(amountInBaseCurrency);
        }
        paymentDetails.setTax(null);
        paymentDetails.setFee(null);

        Order order = paymentDetails.getOrder();
        order.setOrderStatus(OrderStatus.ORDER_PAYMENT_FAILED);
        orderRepository.save(order);
        paymentDetailsRepository.save(paymentDetails);
        log.info("Payment {} processed for gatewayOrderId={}",
                paymentDetails.getStatus(), dto.getGatewayOrderId());
        // just save dB and do nothing

    }

    @Override
    public void handlePaymentCancelled(PaymentWebhookDto dto) {
        PaymentDetails paymentDetails = paymentDetailsRepository.findByGatewayOrderId(dto.getGatewayOrderId()).orElseThrow(() ->
                new IllegalStateException("Payment not found with GatewayOrderId "+ dto.getGatewayOrderId()));
        if (paymentDetails.getStatus() == OrderStatus.ORDER_PAYMENT_CANCELLED.name()){
            log.info("Payment already marked as {}, skipping update. gatewayOrderId={}",
                    paymentDetails.getStatus(), dto.getGatewayOrderId());
            return;
        }
        paymentDetails.setStatus(OrderStatus.ORDER_PAYMENT_CANCELLED.name());
        paymentDetails.setGatewayPaymentId(dto.getGatewayPaymentId());
        paymentDetails.setEmail(dto.getEmail());
        paymentDetails.setContact(dto.getContact());
        paymentDetails.setMethod(dto.getMethod());
        paymentDetails.setWallet(dto.getWallet());
        paymentDetails.setBank(dto.getBank());
        // Amount may exist even in failure
        if (dto.getAmount() != null) {
            BigDecimal amountInBaseCurrency = BigDecimal.valueOf(dto.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            paymentDetails.setAmount(amountInBaseCurrency);
        }
        paymentDetails.setTax(null);
        paymentDetails.setFee(null);

        Order order = paymentDetails.getOrder();
        order.setOrderStatus(OrderStatus.ORDER_PAYMENT_CANCELLED);
        orderRepository.save(order);
        paymentDetailsRepository.save(paymentDetails);
        log.info("Payment {} processed for gatewayOrderId={}",
                paymentDetails.getStatus(), dto.getGatewayOrderId());

    }

    private void validateInventoryAndFreezePrices(Order order){
        for (OrderItem item: order.getOrderItems()){
            Product product = item.getProduct();
            // check inventory
            if (product.getInventory() < item.getQuantity()){
                throw new StockInsufficientException("Insufficient stock for product: " + product.getName());
            }
            item.setPrice(product.getPrice());
        }
    }

    private void calculateGrandTotal(Order order){
        BigDecimal subTotal = order.getTotalAmount();
        BigDecimal shipping = new BigDecimal("5");
        BigDecimal tax = subTotal.multiply(new BigDecimal(".10"));
        order.setTotalAmount(subTotal.add(shipping).add(tax));
    }

}
