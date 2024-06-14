package server.exception;

public class PersonAlreadyExistsException extends RuntimeException{

    private static final String message = "Person already exists";

    public PersonAlreadyExistsException() {
        super(message);
    }
}
