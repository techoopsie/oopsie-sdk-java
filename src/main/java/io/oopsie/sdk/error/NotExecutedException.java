package io.oopsie.sdk.error;

import io.oopsie.sdk.ResultSet;
import io.oopsie.sdk.Statement;

/**
 * Thrown if {@link Statement} has not yet been executed and therfor no {@link ResultSet}.
 */
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
