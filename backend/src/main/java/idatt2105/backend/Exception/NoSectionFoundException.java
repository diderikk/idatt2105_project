package idatt2105.backend.Exception;

public class NoSectionFoundException extends Exception {
    public NoSectionFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoSectionFoundException(String errorMessage) {
        super(errorMessage);
    }
}
