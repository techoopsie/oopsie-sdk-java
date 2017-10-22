package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementParamException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import org.springframework.http.HttpMethod;

/**
 * Use this class to save persisted ( HTTP POST) entities for a {@link Resource}.
 * Instances of this class are thread safe and can
 * be uses with Site.executeAsync(...)
 * to produce a {@link Future} to fetch related {@link ResultSet}.
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
    }
       
    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId,
            UUID siteId, String apiKey, String cookie)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestMethod(HttpMethod.PUT);
        setRequestBody(attribVals);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookie);
    }
    
    @Override
    public final SaveStatement withParam(String attrib, Object val)
            throws AlreadyExecutedException, StatementParamException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        if(resource.getAllSettableAttributeNames().contains(attrib)) {
            if(attribVals == null) {
                attribVals = new HashMap();
            }
            attribVals.put(attrib, val);
        } else {
            super.withParam(attrib, val);
        }
        return this;
    }
    
    @Override
    public final SaveStatement withParams(Map<String, Object> attribs) throws AlreadyExecutedException, StatementParamException {
        
        attribs.forEach((a,v) -> withParam(a, v));
        return this;
    }
    
    @Override
    public void reset() {
        attribVals = null;
        super.reset();
    }
}