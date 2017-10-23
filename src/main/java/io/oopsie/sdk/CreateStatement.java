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
 * Use this class to create and persist ( HTTP POST) entities for a {@link Resource}.
 * Instances of this class are thread safe and can
 * be uses with {@link Site#executeAsync(io.oopsie.sdk.Statement) }
 * to produce a {@link Future} to fetch related {@link ResultSet}.
 */
public class CreateStatement extends Statement<CreateStatement> {
    
    private Map<String, Object> attribVals;
    
    /**
     * Used internally to initialize a {@link CreateStatement} for a {@link Resource}.
     * 
     * @param resource
     */
    CreateStatement(Resource resource) {
        super(resource);
        setRequestMethod(HttpMethod.POST);
    }
   
    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId,
            UUID siteId, String apiKey, String cookie)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestBody(attribVals);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookie);
    }
    
    @Override
    public final CreateStatement withParam(String attrib, Object val)
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
    public final CreateStatement withParams(Map<String, Object> attribs) throws AlreadyExecutedException, StatementParamException {
        
        attribs.forEach((a,v) -> withParam(a, v));
        return this;
    }

    @Override
    public void reset() {
        attribVals = null;
        super.reset();
    }
}