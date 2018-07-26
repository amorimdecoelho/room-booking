package com.mls.roombooking.controllers;

import com.mls.roombooking.exceptions.OfficeHourException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController
        extends ResponseEntityExceptionHandler {

    private final Log logger = LogFactory.getLog(this.getClass());

    @ExceptionHandler(value
            = { Throwable.class })
    protected ResponseEntity<Object> handleError(
            RuntimeException ex, WebRequest request) {
        final String msg = "Error processing the request";
        logger.error(msg, ex);
        return handleExceptionInternal(ex, msg,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ExceptionHandler(value =  java.lang.IllegalArgumentException.class)
    protected ResponseEntity<Object> handleInput(
            RuntimeException ex, WebRequest request) {
        final String msg = "Error parsing the input";
        logger.error(msg, ex);
        return handleExceptionInternal(ex, msg,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value =  OfficeHourException.class)
    protected ResponseEntity<Object> handleInvalidOfficeHours(
            RuntimeException ex, WebRequest request) {
        final String msg = "Error parsing the office hours: " + ex.getMessage();
        logger.error(msg, ex);
        return handleExceptionInternal(ex, msg,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}