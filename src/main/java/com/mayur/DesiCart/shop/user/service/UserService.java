package com.mayur.DesiCart.shop.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;
import com.mayur.DesiCart.shop.user.dto.CreateUserRequestDto;
import com.mayur.DesiCart.shop.user.dto.ResetPasswordDto;
import com.mayur.DesiCart.shop.user.dto.UserDto;
import com.mayur.DesiCart.shop.user.dto.UserUpdateDto;
import com.mayur.DesiCart.shop.user.exceptions.UserNotFoundException;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.models.UserMapper;
import com.mayur.DesiCart.shop.user.repositories.UserRepository;

import java.util.Optional;
@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequestDto request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByUserId(request.getUserId()))
                .map(userRequest -> {
                    User user = new User();
                    user.setUserId(userRequest.getUserId());
                    user.setFirstName(userRequest.getFirstName());
                    user.setLastName(userRequest.getLastName());
                    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("Unable to save the user"));
    }

    @Override
    public User updateUser(UserUpdateDto request, Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(() -> new RuntimeException("Unable to update user"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () -> {
            throw new RuntimeException("User not found to delete");
        });

    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        return userRepository.findByUserId(userid).orElseThrow(() -> new ResourceNotFoundException("User not found in getAuthenticatedUser: "+userid));
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())){
            throw new IllegalStateException("Passwords donot match, make sure you enter correctly");
        }

        try {
            User user = userRepository.findByUserId(resetPasswordDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with email: "+resetPasswordDto.getUserId()));
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalStateException("technical error while updating password of user: "+resetPasswordDto.getConfirmPassword());
        }
    }

    @Override
    public UserDto convertUserToDto(User user) {
        return userMapper.userToUserDto(user);
    }
}
