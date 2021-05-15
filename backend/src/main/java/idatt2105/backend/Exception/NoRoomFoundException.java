package idatt2105.backend.Exception;

public class NoRoomFoundException extends Exception {
    public NoRoomFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoRoomFoundException(String errorMessage) {
        super(errorMessage);
    }
}
