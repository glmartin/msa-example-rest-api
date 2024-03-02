package glmartin.java.restapi.controllers;

import java.util.Date;

public record ControllerErrorResponse(int statusCode, Date timestamp,String message) {}