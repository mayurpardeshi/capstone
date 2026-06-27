package com.mayur.DesiCart.shop.user.service;

import com.mayur.DesiCart.shop.user.dto.CreateUserRequestDto;
import com.mayur.DesiCart.shop.user.dto.ResetPasswordDto;
import com.mayur.DesiCart.shop.user.dto.UserDto;
import com.mayur.DesiCart.shop.user.dto.UserUpdateDto;
import com.mayur.DesiCart.shop.user.models.User;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequestDto request);
    User updateUser(UserUpdateDto request, Long userId);
    void deleteUser(Long userId);
    User getAuthenticatedUser();
    void resetPassword(ResetPasswordDto resetPasswordDto);

    UserDto convertUserToDto(User user);
}
