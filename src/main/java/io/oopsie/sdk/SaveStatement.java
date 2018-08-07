package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementParamException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpMethod;

/**
 * Use this class to save persisted ( HTTP POST) entities for a {@link Resource}.
 */
public class SaveStatement extends Statement<SaveStatement> {

    private Map<String, Object> attribVals;
    
    /**
     * Used internally to initialize a {@link SaveStatement} for a {@link Resource}.
     * 
     * @param resource
     * @param pk 
     * @throws StatementParamException
     */
    SaveStatement(Resource resource) {
        super(resource);
        setRequestMethod(HttpMethod.PUT);
    }
       
    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId,
            UUID siteId, String apiKey, List<String> cookies)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestBody(attribVals);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookies);
    }
    
    @Override
    public final SaveStatement withParam(String param, Object val)
            throws AlreadyExecutedException, StatementParamException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        // SaveStatement needs the attribute params in request body
        if(resource.getAllSettableAttributeNames().contains(param)) {
            if(attribVals == null) {
                attribVals = new HashMap();
            }
            attribVals.put(param, val);
        } else {
            super.withParam(param, val);
        }
        return this;
    }
    
    @Override
    public final SaveStatement withParams(Map<String, Object> params)
            throws AlreadyExecutedException, StatementParamException {
        
        params.forEach((a,v) -> withParam(a, v));
        return this;
    }
    
    @Override
    public void reset() {
        attribVals = null;
        super.reset();
    }
}