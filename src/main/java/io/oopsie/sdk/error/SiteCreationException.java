package io.oopsie.sdk.error;

import io.oopsie.sdk.Site;

/**
 * Thrown when a {@link Site} could not be created.
 */
public class SiteCreationException extends OopsieSiteException {

    public SiteCreationException() {
    }

    public SiteCreationException(String message) {
        super(message);
    }

    public SiteCreationException(Throwable cause) {
        super(cause);
    }

    public SiteCreationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
