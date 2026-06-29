package com.mayur.DesiCart.shop.user.dto;


import lombok.Data;

@Data
public class CreateUserRequestDto {
    private String firstName;
    private String lastName;
    private String userId;
    private String password;
}
