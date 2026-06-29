package com.mayur.DesiCart.shop.user.models;

import org.mapstruct.Mapper;

import com.mayur.DesiCart.shop.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userToUserDto(User user);
    User userDtoToUser(UserDto userDto);
}
