package com.mayur.DesiCart.shop.cartAndCheckout.dtos;

import com.mayur.DesiCart.shop.cartAndCheckout.models.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponseDto {
    private String sessionId;
    private String sessionUrl;
    private PaymentStatus paymentStatus;
    private String message;

    public StripeResponseDto(String sessionId){
        this.sessionId = sessionId;
    }
}
