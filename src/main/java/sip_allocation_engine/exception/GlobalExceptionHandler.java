package sip_allocation_engine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FundNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleFundNotFound(FundNotFoundException ex) {
        return ex.getMessage();
    }


    @ExceptionHandler(NAVAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleNAVAlreadyExists(NAVAlreadyExistsException ex) {
        return ex.getMessage();
    }
}