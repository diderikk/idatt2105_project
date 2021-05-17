package idatt2105.backend.Exception;

public class SectionNameInRoomAlreadyExistsException extends Exception {
    public SectionNameInRoomAlreadyExistsException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public SectionNameInRoomAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
