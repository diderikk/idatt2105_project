package idatt2105.backend.Exception;

public class NoReservationFoundException extends Exception {
    public NoReservationFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

    public NoReservationFoundException(String errorMessage) {
        super(errorMessage);
    }
}
