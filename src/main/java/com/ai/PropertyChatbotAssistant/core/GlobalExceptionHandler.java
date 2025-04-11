package com.ai.PropertyChatbotAssistant.core;

import com.ai.PropertyChatbotAssistant.dto.ErrorResponse;
import com.ai.PropertyChatbotAssistant.exception.PropertyChatbotAssistantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PropertyChatbotAssistantException.class)
    public ResponseEntity<ErrorResponse> handleException(PropertyChatbotAssistantException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Something went wrong",
                e.getLocalizedMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
