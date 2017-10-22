package io.oopsie.sdk.error;

import io.oopsie.sdk.Statement;

/**
 * Thrown if {@link Statement} has been executed.
 */
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
