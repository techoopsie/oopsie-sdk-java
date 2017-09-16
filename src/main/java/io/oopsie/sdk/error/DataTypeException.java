package io.oopsie.sdk.error;

public class DataTypeException extends OopsieSiteException {

    public DataTypeException() {
    }

    public DataTypeException(String message) {
        super(message);
    }

    public DataTypeException(Throwable cause) {
        super(cause);
    }

    public DataTypeException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
