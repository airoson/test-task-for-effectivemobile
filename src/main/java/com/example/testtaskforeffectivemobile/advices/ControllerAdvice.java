package com.example.testtaskforeffectivemobile.advices;

import com.example.testtaskforeffectivemobile.exception.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.TreeMap;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<?> handleValueIsTakenException(ServiceException e){
        Map<String, String> message = new TreeMap<>();
        message.put("error", e.getMessage());
        return ResponseEntity.ok(message);
    }
}
