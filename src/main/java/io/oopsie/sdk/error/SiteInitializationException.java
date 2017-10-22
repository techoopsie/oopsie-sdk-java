package io.oopsie.sdk.error;

import io.oopsie.sdk.Site;

/**
 * Trrown When a {@link Site} could not be initialized.
 */
public class SiteInitializationException extends OopsieSiteException {

    public SiteInitializationException() {
    }

    public SiteInitializationException(String message) {
        super(message);
    }

    public SiteInitializationException(Throwable cause) {
        super(cause);
    }

    public SiteInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
