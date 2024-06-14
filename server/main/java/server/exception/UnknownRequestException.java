package server.exception;

public class UnknownRequestException extends RuntimeException{

    private static final String message = "Unknown request";

    public UnknownRequestException() {
        super(message);
    }
}
