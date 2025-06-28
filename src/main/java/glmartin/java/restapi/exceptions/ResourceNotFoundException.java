package glmartin.java.restapi.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String path) {
        super(String.format("Could not find resource: %s", path));
    }
}