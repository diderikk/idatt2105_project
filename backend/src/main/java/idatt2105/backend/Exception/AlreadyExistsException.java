package idatt2105.backend.Exception;

/**
 * Exception cast when same email address is being used
 * multiple times. Can be when editing user information,
 * or adding new information. 
 */
public class AlreadyExistsException extends Exception{
    public AlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
