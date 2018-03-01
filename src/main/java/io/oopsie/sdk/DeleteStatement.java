package io.oopsie.sdk;

import io.oopsie.sdk.error.StatementParamException;
import org.springframework.http.HttpMethod;

/**
 * Use this class to delete a persisted entity for a {@link Resource}.
 */
public class DeleteStatement extends Statement<DeleteStatement> {
/**
     * Used internally to initialize a {@link DeleteStatement} for a {@link Resource}.
     * 
     * @param resource
     * @throws StatementParamException
     */
    DeleteStatement(Resource resource) {
        super(resource);
        setRequestMethod(HttpMethod.DELETE);
    }
}
