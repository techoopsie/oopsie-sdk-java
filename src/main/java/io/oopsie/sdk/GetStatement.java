package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.Future;
import org.springframework.http.HttpMethod;

/**
 * Use this class to fetch (HTTP GET) entities stored in a remote OOPSIE cloud for a
 * specific {@link Resource}. Instances of this class are thread safe and can
 * be uses with {@link Site#executeAsync(io.oopsie.sdk.model.Statement)}
 * to produce a {@link Future} to fetch related {@link ResultSet}.
 * 
 */
public class GetStatement extends Statement<GetStatement> {
    
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
     * @param limit
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException
     * @throws StatementException
     */
    public final GetStatement limit(int limit) throws AlreadyExecutedException, StatementException {
        
        if(limit < 0 || limit > 1000) {
            throw new StatementException("Limit must be between 0 - 1000");
        }
        return withParam("_limit", limit);
    }
    
    /**
     * When fetching data for {@link Resesource} also fetch all relation data.
     * @return this {@link GetStatement}
     * @see #reset() 
     */
    public GetStatement expandRelations() {
        return withParam("_expandRelations", true);
    }
}
