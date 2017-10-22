package io.oopsie.sdk.error;

/**
 * Base exception.
 */
public class OopsieSiteException extends RuntimeException {

    public OopsieSiteException() {
    }

    public OopsieSiteException(String message) {
        super(message);
    }

    public OopsieSiteException(Throwable cause) {
        super(cause);
    }

    public OopsieSiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
