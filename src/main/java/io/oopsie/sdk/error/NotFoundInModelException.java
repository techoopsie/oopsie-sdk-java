package io.oopsie.sdk.error;

import io.oopsie.sdk.Application;
import io.oopsie.sdk.Resource;

/**
 * Thrown if a {@link Application} or {@link Resource} can't be found.
 */
public class NotFoundInModelException extends OopsieSiteException {

    public NotFoundInModelException() {
    }

    public NotFoundInModelException(String message) {
        super(message);
    }

    public NotFoundInModelException(Throwable cause) {
        super(cause);
    }

    public NotFoundInModelException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
