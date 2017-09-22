package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Future;
import org.springframework.http.HttpMethod;

/**
 * Use this class to create and persist ( HTTP POST) entities for a {@link Resource}.
 * Instances of this class are thread safe and can
 * be uses with {@link Site#executeAsync(io.oopsie.sdk.model.Statement)}
 * to produce a {@link Future} to fetch related {@link ResultSet}.
 */
public class CreateStatement extends Statement {

    private Map<String, Object> attribVals = new HashMap();

    /**
     * Used internally to initialize a {@link CreateStatement} for a {@link Resource}.
     * 
     * @param apUri
     * @param custId
     * @param siteId 
     */
    CreateStatement(Resource resource) {
        super(resource);
    }
       
    /**
     * Adds the named attrib value. If an attribute value already is
     * mapped to passed in name the old value will be replaced.
     * 
     * @param attrib the name of the attribute to withParams
     * @param val the value of the attribute
     * @return this {@link CreateStatement}
     * @throws AlreadyExecutedException
     * @throws StatementException
     * @see CreateStatement#reset() 
     * @see #reset() 
     */
    public final CreateStatement withParam(String attrib, Object val) throws AlreadyExecutedException, StatementException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        if(ReservedAttributeNames.containsName(attrib)) {
            throw new StatementException("Attribute name " + attrib + " is reserved.");
        }
        
        if(!resource.getAllAttributeNames().contains(attrib)) {
            throw new StatementException("Attribute name " + attrib + " is not part of this resource model");
        }
        
        if(attribVals == null) {
            attribVals = new HashMap();
        }
        attribVals.put(attrib, val);
        return this;
    }
    
    /**
     * Adds the attrib values mapped to the names in passed in map. If an attribute value already
     * is mapped to any of the passed in names the old value will be replaced with the
     * new mapped value.
     * 
     * @param attribs a map of attributes and values
     * @return this {@link CreateStatement}
     * @see CreateStatement#reset()
     * @throws StatementException
     * @throws AlreadyExecutedException
     * @see #reset() 
     */
    public final CreateStatement withParams(Map<String, Object> attribs) throws AlreadyExecutedException, StatementException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        attribs.forEach((a,v) -> withParam(a, v));
        return this;
    }
    
    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, UUID customerId, UUID siteId, String apiKey, String cookie)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestMethod(HttpMethod.POST);
        setRequestBody(attribVals);
        return super.execute(baseApiUri, customerId, siteId, apiKey, cookie);
    }

    /**
     * Resets this statement to an initial state as if you where creating a
     * new instance with the constructor {@link CreateStatement#CreateStatement(io.oopsie.sdk.model.Resource)}.
     * Note, this method calls super.reset() so any result this statement holds will be lost.
     * @see Statement#reset()
     */
    @Override
    public void reset() {
        attribVals = null;
        super.reset();
    }
}