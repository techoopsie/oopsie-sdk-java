package io.oopsie.sdk.error;

public class NotExecutedException extends OopsieSiteException {

    public NotExecutedException() {
    }

    public NotExecutedException(String message) {
        super(message);
    }

    public NotExecutedException(Throwable cause) {
        super(cause);
    }

    public NotExecutedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
