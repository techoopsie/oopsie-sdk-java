package io.oopsie.sdk.error;

/**
 *
 * @author nicolas
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
