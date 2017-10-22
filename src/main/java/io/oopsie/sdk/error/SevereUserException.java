package io.oopsie.sdk.error;

/**
 * Thrown when a severe exception response, usually a 500, has beed received
 * from OOPSIE api.
 */
public class SevereUserException extends OopsieSiteException{

    public SevereUserException() {
    }

    public SevereUserException(String message) {
        super(message);
    }

    public SevereUserException(Throwable cause) {
        super(cause);
    }

    public SevereUserException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
