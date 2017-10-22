package io.oopsie.sdk.error;

import io.oopsie.sdk.Row;

/**
 * Thrown if the value in a {@link Row} can't be cast to specified type.
 */
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
