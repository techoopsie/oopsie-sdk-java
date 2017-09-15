package io.oopsie.sdk.model;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Creates and persists (POST) an entity for related {@link Resource}.
 * Instances of this class are thread safe.
 */
public class CreateStatement extends Statement {

    private final Map<String, Object> attribVals = new HashMap();
    private static final Set<String> reservedAttribNames = new HashSet();
    static {
        reservedAttribNames.add("cid");
        reservedAttribNames.add("eid");
        reservedAttribNames.add("cra");
        reservedAttribNames.add("crb");
        reservedAttribNames.add("cha");
        reservedAttribNames.add("chb");
    }

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
     * Sets the named attrib value. If an attribute value already is set
     * and mapped to passed in name the old value will be replaced.
     * 
     * @param attrib the name of the attribute to set
     * @param val the value of the attribute
     * @return this {@link CreateStatement}
     * @throws AlreadyExecutedException
     * @throws StatementException
     * @see CreateStatement#reset() 
     */
    public final CreateStatement set(String attrib, Object val) throws AlreadyExecutedException, StatementException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        if(reservedAttribNames.contains(attrib)) {
            throw new StatementException("Attribute name " + attrib + " is reserved.");
        }
        
        if(!resource.getAllAttributeNames().contains(attrib)) {
            throw new StatementException("Attribute name " + attrib + " is not part of this resource model");
        }
        
        attribVals.put(attrib, val);
        return this;
    }
    
    /**
     * Sets the attrib values mapped to the names in passed in map. If an attribute value already is set
     * and mapped to any of the passed in names the old value will be replaced with the
     * new mapped value.
     * 
     * @param attribs a map of attributes and values
     * @return this {@link CreateStatement}
     * @see CreateStatement#reset() 
     */
    public final CreateStatement set(Map<String, Object> attribs) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        attribs.forEach((a,v) -> set(a, v));
        return this;
    }
    
    @Override
    protected synchronized final Result execute(URI baseApiUri, HttpHeaders baseHeaders)
            throws AlreadyExecutedException, StatementExecutionException {
        
        setRequestMethod(HttpMethod.POST);
        setRequestBody(attribVals);
        return super.execute(baseApiUri, baseHeaders);
    }
}