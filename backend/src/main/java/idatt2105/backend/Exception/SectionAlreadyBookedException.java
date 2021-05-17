package idatt2105.backend.Exception;

public class SectionAlreadyBookedException extends Exception{
    public SectionAlreadyBookedException(String errorMessage) {
        super(errorMessage);
    }
}
