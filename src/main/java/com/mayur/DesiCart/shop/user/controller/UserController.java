package com.mayur.DesiCart.shop.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.mayur.DesiCart.shop.common.dto.ApiResponse;
import com.mayur.DesiCart.shop.user.dto.CreateUserRequestDto;
import com.mayur.DesiCart.shop.user.dto.UserDto;
import com.mayur.DesiCart.shop.user.dto.UserUpdateDto;
import com.mayur.DesiCart.shop.user.exceptions.UserNotFoundException;
import com.mayur.DesiCart.shop.user.models.User;
import com.mayur.DesiCart.shop.user.service.IUserService;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/${api_prefix}/users")
@Tag(name = "User Management", description = "APIs for creating and managing users")
public class UserController {
    private final IUserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Operation(summary = "Create a new User")
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequestDto request){
        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUserToDto(user);
            return ResponseEntity.ok(new ApiResponse("User created succesfully", userDto));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            UserDto dto = userService.convertUserToDto(user);
            logger.info("Fetched successfully user details for : {}", userId );
            return ResponseEntity.ok(new ApiResponse("User fetched successfully", dto));
        } catch (UserNotFoundException e){
            logger.error("User not found : {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateDto userUpdateDto, @PathVariable Long userId) {
        try {
            User user = userService.updateUser(userUpdateDto, userId);
            UserDto userDto = userService.convertUserToDto(user);
            logger.info("Updated user details successfully for : {}", userId );
            return ResponseEntity.ok(new ApiResponse("Update success", userDto));

        } catch (Exception e) {
            logger.error("Technical error while updating user with userId {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("User Delete successful", null));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // 1. Open to everyone
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> home(){
        return ResponseEntity.ok(new ApiResponse("All Access - Public", null));
    }

    // 2. Requires ROLE_USER or ROLE_ADMIN
    @GetMapping("/u")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse> user(){
        return ResponseEntity.ok(new ApiResponse("User/Admin Access Only", null));
    }

    // 3. Requires ROLE_ADMIN strictly
    @GetMapping("/a")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> admin(Authentication authentication){
        System.out.println("User: " + authentication.getName());
        System.out.println("Authorities: " + authentication.getAuthorities());
        return ResponseEntity.ok(new ApiResponse("Admin Access Only", null));
    }
}
