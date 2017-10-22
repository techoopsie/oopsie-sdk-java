package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementParamException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.UUID;
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
    }
       
    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId,
            UUID siteId, String apiKey, String cookie)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestMethod(HttpMethod.DELETE);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookie);
    }
}
