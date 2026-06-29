package com.mayur.DesiCart.shop.order.payments.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mayur.DesiCart.shop.order.models.Order;
import com.mayur.DesiCart.shop.order.orderRepository.OrderRepository;

import java.math.BigDecimal;

@Controller // <--- Ensure this is NOT @RestController
@RequestMapping("/view/payments")
public class PaymentInitiator {
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/pay/{orderId}")
    public String renderPaymentPage(@PathVariable Long orderId, Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Ensure the order is in the correct state
        if (order.getGatewayOrderId() == null) {
            throw new RuntimeException("Payment not initiated yet for this order");
        }

        model.addAttribute("rzpOrderId", order.getGatewayOrderId());
        model.addAttribute("amount", order.getTotalAmount().multiply(new BigDecimal(100)).longValue()); // RZP expects paise
        model.addAttribute("currency", "INR");
        model.addAttribute("apiKey", "rzp_test_Rygdi2QEuUjTJg"); // Use your actual test key
        model.addAttribute("orderId", order.getId());

        return "payment_page";
    }
}
