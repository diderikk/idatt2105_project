package idatt2105.backend.Exception;

public class EmailAlreadyExistsException extends Exception{
    public EmailAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
