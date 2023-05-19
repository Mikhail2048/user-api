package org.example.api;

import org.example.api.response.ErrorDto;
import org.example.exception.ClientSideException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(ClientSideException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto clientSideError(ClientSideException exception) {
        return new ErrorDto(exception.getMessage());
    }
}