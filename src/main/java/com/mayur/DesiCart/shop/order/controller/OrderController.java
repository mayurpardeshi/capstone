package com.mayur.DesiCart.shop.order.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentWebhookDto;
import com.mayur.DesiCart.shop.common.dto.ApiResponse;
import com.mayur.DesiCart.shop.order.dto.OrderDto;
import com.mayur.DesiCart.shop.order.parsers.RazorpayWebhookParser;
import com.mayur.DesiCart.shop.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/user/place-order")
    public ResponseEntity<ApiResponse> createOrder(){
        // place order handles validations
        OrderDto orderDto = orderService.placeOrder();
        return ResponseEntity.ok().body(new ApiResponse<>("Order placed successfully", orderDto));
    }

    @PostMapping("/checkout-order")
    public ResponseEntity<ApiResponse> checkoutOrder(Long orderId){
        OrderDto response = orderService.checkout(orderId);
        return ResponseEntity.ok().body(new ApiResponse<>("Order Checked out success", response));
    }

    @PostMapping("/initiate-payment")
    public ResponseEntity<ApiResponse> initiatePayment(@RequestParam Long orderId, @RequestParam String gateway){
        PaymentInitializationResponse response = orderService.initiatePayment(orderId, gateway);
        return ResponseEntity.ok().body(new ApiResponse<>("Payment initialization success", response));
    }

    @PostMapping("/razorpay")
    public ResponseEntity<String> handleRazorpayWebhook(@RequestBody JsonNode root) {

        String event = root.path("event").asText();

        PaymentWebhookDto dto = RazorpayWebhookParser.parse(root);

        switch (event) {
            case "order.paid":
            case "payment.captured":
                orderService.handlePaymentSuccess(dto);
                break;

            case "payment.failed":
            case "order.payment_failed":
                orderService.handlePaymentFailure(dto);
                break;

            case "order.cancelled":
                orderService.handlePaymentCancelled(dto);
                break;

            default:
                return ResponseEntity.ok("Event ignored");
        }

        return ResponseEntity.ok("Processed");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId){
        OrderDto orderDto = orderService.getOrder(orderId);
        return ResponseEntity.ok().body(new ApiResponse<>("Order found", orderDto));

    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getOrdersByUserId(@PathVariable Long userId){
        List<OrderDto> orders = orderService.getOrderByUserId(userId);
        return ResponseEntity.ok().body(new ApiResponse<>("User orders ", orders));
    }
}
