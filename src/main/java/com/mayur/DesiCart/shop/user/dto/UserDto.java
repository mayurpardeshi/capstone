package com.mayur.DesiCart.shop.user.dto;

import com.mayur.DesiCart.shop.user.models.Role;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
