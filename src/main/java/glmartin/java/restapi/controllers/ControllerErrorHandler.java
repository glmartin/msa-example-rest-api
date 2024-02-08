package glmartin.java.restapi.controllers;

import glmartin.java.restapi.controllers.exceptions.ControllerErrorResponse;
import glmartin.java.restapi.controllers.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestControllerAdvice
public class ControllerErrorHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ControllerErrorResponse resourceNotFound(ResourceNotFoundException ex) {
        return  new ControllerErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage());
    }

}