package io.oopsie.sdk.error;

import io.oopsie.sdk.Site;

/**
 * Thrown if the {@link Site} identifications could be parsed.
 */
public class IllegalIdentificationException extends OopsieSiteException {

    public IllegalIdentificationException() {
    }

    public IllegalIdentificationException(String message) {
        super(message);
    }

    public IllegalIdentificationException(Throwable cause) {
        super(cause);
    }

    public IllegalIdentificationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
