package com.mayur.DesiCart.shop.common.ControllerAdvice;

import jakarta.persistence.EntityNotFoundException;
import kotlin.io.AccessDeniedException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mayur.DesiCart.shop.common.dto.ApiResponse;
import com.mayur.DesiCart.shop.order.exceptions.ProductOutOfStockException;
import com.mayur.DesiCart.shop.product.exception.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFound(ResourceNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(exception.getMessage(), null));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse("You do not have permission to perform this action. Check with admin", null));
    }

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationErrors(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>("Validation failed", errors));
    }

    // Handle business logic failures(eg. empty cart, out of stock)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse> handleIllegalState(IllegalStateException exception){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(exception.getMessage(), null));
    }

    // catch any unexpected server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneralException(Exception exception){
        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>("An unexpected error occured", null));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> handleEntityNotFound(EntityNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(exception.getMessage(), null));
    }

    @ExceptionHandler(IncorrectResultSizeDataAccessException.class)
    public ResponseEntity<ApiResponse> handleDuplicateDataException(IncorrectResultSizeDataAccessException e){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>("Internal Data Error: Multiple records found where only one was expected.", null));
    }

    @ExceptionHandler(ProductOutOfStockException.class)
    public ResponseEntity<ApiResponse> handleOutOfStock(ProductOutOfStockException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 Conflict
                .body(new ApiResponse(ex.getMessage(), "OUT_OF_STOCK"));
    }

    @ExceptionHandler(org.springframework.data.mapping.PropertyReferenceException.class)
    public ResponseEntity<ApiResponse> handlePropertyReferenceException(PropertyReferenceException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse("Invalid sort property: " + e.getPropertyName(), null));
    }
}
