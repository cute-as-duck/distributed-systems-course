package server.exception;

public class PersonNotFoundException extends RuntimeException{

    private static final String message = "Person not found";

    public PersonNotFoundException() {
        super(message);
    }
}
