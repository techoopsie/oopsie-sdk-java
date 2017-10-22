package io.oopsie.sdk.error;

import io.oopsie.sdk.Statement;

/**
 * Thrown when {@link Statement} doesn't accept the param.
 */
public class StatementParamException extends OopsieSiteException {

    public StatementParamException() {
    }

    public StatementParamException(String message) {
        super(message);
    }

    public StatementParamException(Throwable cause) {
        super(cause);
    }

    public StatementParamException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
