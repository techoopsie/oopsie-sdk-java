package io.oopsie.sdk.error;

public class ModelException extends OopsieSiteException {

    public ModelException() {
    }

    public ModelException(String message) {
        super(message);
    }

    public ModelException(Throwable cause) {
        super(cause);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
