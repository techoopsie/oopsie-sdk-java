package io.oopsie.sdk.error;

import io.oopsie.sdk.Site;

/**
 * Thrown if the {@link Site} API uri is not an URI.
 */
public class ApiURIException extends OopsieSiteException {

    public ApiURIException() {
    }

    public ApiURIException(String message) {
        super(message);
    }

    public ApiURIException(Throwable cause) {
        super(cause);
    }

    public ApiURIException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
