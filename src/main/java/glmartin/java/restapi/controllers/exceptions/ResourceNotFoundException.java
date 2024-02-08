package glmartin.java.restapi.controllers.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String path) {
        super("Could not find resource: " + path);
    }
}