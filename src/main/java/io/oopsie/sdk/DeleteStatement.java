package io.oopsie.sdk;

import io.oopsie.sdk.error.StatementParamException;
import org.springframework.http.HttpMethod;

public class DeleteStatement extends Statement<DeleteStatement> {
/**
     * Used internally to initialize a {@link DeleteStatement} for a {@link Resource}.
     * 
     * @param resource
     * @param pk 
     * @throws StatementParamException
     */
    DeleteStatement(Resource resource) {
        super(resource);
        setRequestMethod(HttpMethod.DELETE);
    }
}
