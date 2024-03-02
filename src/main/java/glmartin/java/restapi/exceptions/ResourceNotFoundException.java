package glmartin.java.restapi.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String path) {
        super("Could not find resource: " + path);
    }
}