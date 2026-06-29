package com.mayur.DesiCart.shop.cartAndCheckout.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mayur.DesiCart.shop.cartAndCheckout.dtos.PaymentInitializationResponse;
import com.mayur.DesiCart.shop.cartAndCheckout.services.CheckoutService;
import com.mayur.DesiCart.shop.common.dto.ApiResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/checkout")
public class CheckoutController {
    @Autowired
    private final CheckoutService checkoutService;

    @PostMapping("/initiate-payment")
    public ResponseEntity<ApiResponse> initiatePayment(@RequestParam Long orderId, @RequestParam String gateway){
        PaymentInitializationResponse response = checkoutService.checkout(orderId, gateway);
        return ResponseEntity.ok().body(new ApiResponse<>("Payment initialization success", response));
    }
}
