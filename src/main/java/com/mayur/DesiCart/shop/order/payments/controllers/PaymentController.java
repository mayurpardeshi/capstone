package com.mayur.DesiCart.shop.order.payments.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mayur.DesiCart.shop.common.dto.ApiResponse;
import com.mayur.DesiCart.shop.order.payments.dtos.PaymentVerifyRequest;
import com.mayur.DesiCart.shop.order.payments.services.PaymentService;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentVerificationService;

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@RequestBody PaymentVerifyRequest request){
        paymentVerificationService.verifyPayment(request);
        return ResponseEntity.ok().body(new ApiResponse<>("Payment verified successfully", null));
    }
}
