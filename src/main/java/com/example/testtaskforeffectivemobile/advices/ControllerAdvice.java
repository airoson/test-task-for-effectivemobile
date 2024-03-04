package com.example.testtaskforeffectivemobile.advices;

import com.example.testtaskforeffectivemobile.dtos.ServiceErrorMessage;
import com.example.testtaskforeffectivemobile.exception.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ServiceErrorMessage> handleValueIsTakenException(ServiceException e){
        return new ResponseEntity<>(new ServiceErrorMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
