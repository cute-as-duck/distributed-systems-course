package server.exception;

public class NotAuthenticatedException extends RuntimeException{

    private static final String message = "Not authenticated";

    public NotAuthenticatedException() {
        super(message);
    }
}
