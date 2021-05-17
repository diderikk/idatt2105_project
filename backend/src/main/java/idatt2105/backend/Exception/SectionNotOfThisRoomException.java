package idatt2105.backend.Exception;

public class SectionNotOfThisRoomException extends Exception {
    public SectionNotOfThisRoomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public SectionNotOfThisRoomException(String errorMessage) {
        super(errorMessage);
    }
}
