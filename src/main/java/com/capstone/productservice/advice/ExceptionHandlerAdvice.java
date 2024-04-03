package com.capstone.productservice.advice;

import com.capstone.productservice.dtos.ArithmeticExceptionDto;
import com.capstone.productservice.dtos.ArrayIndexOutOfBoundExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ArithmeticExceptionDto> handleArithmeticException(){
        ArithmeticExceptionDto arithmeticExceptionDto = new ArithmeticExceptionDto();
        arithmeticExceptionDto.setMessage("Something went wrong");
        return new ResponseEntity<>(arithmeticExceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
    public ResponseEntity<ArrayIndexOutOfBoundExceptionDto> handleArrayIndexOutOfBoundException(){
        ArrayIndexOutOfBoundExceptionDto dto = new ArrayIndexOutOfBoundExceptionDto();
        dto.setMessage("error");
        dto.setDetails("details");
        return new ResponseEntity<>(dto,HttpStatus.BAD_REQUEST);
    }
}
