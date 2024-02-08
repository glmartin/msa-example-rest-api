package glmartin.java.restapi.controllers;

import glmartin.java.restapi.controllers.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ControllerErrorHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResourceNotFoundException resourceNotFound(ResourceNotFoundException ce) {
        return ce;
    }

}