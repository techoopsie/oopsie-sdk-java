package io.oopsie.sdk.error;

public class StatementException extends OopsieSiteException {

    public StatementException() {
    }

    public StatementException(String message) {
        super(message);
    }

    public StatementException(Throwable cause) {
        super(cause);
    }

    public StatementException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
