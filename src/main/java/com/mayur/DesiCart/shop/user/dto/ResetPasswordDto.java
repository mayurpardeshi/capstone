package com.mayur.DesiCart.shop.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {
    private String userId;
    private String newPassword;
    private String confirmPassword;
}
