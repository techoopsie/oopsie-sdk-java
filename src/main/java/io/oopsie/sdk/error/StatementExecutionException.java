package io.oopsie.sdk.error;

public class StatementExecutionException extends OopsieSiteException {

    public StatementExecutionException() {
    }

    public StatementExecutionException(String message) {
        super(message);
    }

    public StatementExecutionException(Throwable cause) {
        super(cause);
    }

    public StatementExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
