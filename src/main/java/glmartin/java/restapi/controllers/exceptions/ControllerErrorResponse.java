package glmartin.java.restapi.controllers.exceptions;

import java.util.Date;

public record ControllerErrorResponse(int statusCode, Date timestamp,String message) {}