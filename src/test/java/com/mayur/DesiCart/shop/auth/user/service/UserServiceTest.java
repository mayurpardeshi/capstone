package com.mayur.DesiCart.shop.auth.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mayur.DesiCart.shop.user.dto.CreateUserRequestDto;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.models.UserMapper;
import com.mayur.DesiCart.shop.user.repositories.UserRepository;
import com.mayur.DesiCart.shop.user.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private CreateUserRequestDto userRequestDto;

    @BeforeEach
    void setup(){
        userRequestDto = new CreateUserRequestDto();
        userRequestDto.setUserId("mayur@google.com");
        userRequestDto.setFirstName("Mayur");
        userRequestDto.setLastName("Pardeshi");
        userRequestDto.setPassword("Desi@123");
    }

    @Test
    void getUserById() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("1Test");
        user.setUserId("tiger@abc.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1Test", result.getFirstName());
    }

    @Test
    void getUserById_whenUserDoesNotExists_shouldThrowException(){
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createUserWhenEmailDoesNotExists() {
        when(userRepository.existsByUserId("abc@google.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User savedUser = userService.createUser(userRequestDto);

        assertNotNull(savedUser);
        assertEquals("abc@google.com", savedUser.getUserId());
    }
}