package io.oopsie.sdk.error;

public class AlreadyExecutedException extends OopsieSiteException {

    public AlreadyExecutedException() {
    }

    public AlreadyExecutedException(String message) {
        super(message);
    }

    public AlreadyExecutedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExecutedException(Throwable cause) {
        super(cause);
    }
    
}
