package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementParamException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;
import org.springframework.http.HttpMethod;

/**
 * Use this class to fetch (HTTP GET) entities stored in a remote OOPSIE cloud for a
 * specific {@link Resource}. Instances of this class are thread safe and can
 * be uses with Site.executeAsync(...).
 * to produce a {@link Future} to fetch related {@link ResultSet}.
 * 
 */
public class GetStatement extends Statement<GetStatement> {
    
    /**
     * Creates a new GetStatement for specified {@link Resource}.
     * @param resource the resource
     */
    GetStatement(Resource resource) {
        super(resource);
    }

    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId,
            UUID siteId, String apiKey, String cookie)
            throws AlreadyExecutedException, StatementExecutionException {

        setRequestMethod(HttpMethod.GET);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookie);
    }
    
    /**
     * Set a new fetch size limit between 0 through 1000. Default is 300. 
     * @param limit the limit
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException if executed
     * @throws StatementParamException if limit is out of range (0-1000)
     */
    public final GetStatement limit(int limit) throws AlreadyExecutedException, StatementParamException {
        
        if(limit < 0 || limit > 1000) {
            throw new StatementParamException("Limit must be between 0 - 1000");
        }
        return withParam("_limit", limit);
    }
    
    /**
     * When fetching data for {@link Resource} also fetch all relation data.
     * @return this {@link GetStatement}
     * @see #reset() 
     */
    public GetStatement expandRelations() {
        return withParam("_expandRelations", true);
    }
}
