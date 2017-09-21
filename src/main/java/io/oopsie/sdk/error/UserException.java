package io.oopsie.sdk.error;

public class UserException extends OopsieSiteException{

    public UserException() {
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(Throwable cause) {
        super(cause);
    }

    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
