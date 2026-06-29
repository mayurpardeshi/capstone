package com.mayur.DesiCart.shop.cartAndCheckout.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.exceptions.StockInsufficientException;
import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.models.OrderItem;
import com.mayur.DesiCart.shop.order.models.OrderStatus;
import com.mayur.DesiCart.shop.order.orderRepository.OrderRepository;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.product.models.Product;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService{
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;


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
    public PaymentInitializationResponse checkout(Long orderId, String gateway) {
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
        orderRepository.save(order);

        // Strategy pattern
        return paymentService.processPayment(order, gateway);
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
