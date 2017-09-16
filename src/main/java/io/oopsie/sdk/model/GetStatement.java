package io.oopsie.sdk.model;

import com.google.common.collect.Sets;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * Use this class to fetch (HTTP GET) entities stored in a remote OOPSIE cloud for a
 * specific {@link Resource}. Instances of this class are thread safe and can
 * be uses with {@link Site#executeAsync(io.oopsie.sdk.model.Statement)}
 * to produce a {@link Future} to fetch related {@link ResultSet}.
 * 
 */
public class GetStatement extends Statement {
    
    private int limit = 300;
    private boolean expandRelations;
    private Set<UUID> entityIds;
    private Map<String, Object> primaryKeyParams;
    
    
    GetStatement(Resource resource) {
        super(resource);
    }
    
    GetStatement(Resource resource, UUID entityIds) {
        super(resource);
        this.entityIds = new HashSet(Sets.newHashSet(entityIds));
    }
    
//    GetStatement(Resource resource, UUID... entityIds) {
//        super(resource);
//        this.entityIds = new HashSet(Sets.newHashSet(entityIds));
//    }
    
    GetStatement(Resource resource, Map<String, Object> queryParams, UUID... entityIds) {
        super(resource);
        this.entityIds = new HashSet(Sets.newHashSet(entityIds));
        this.primaryKeyParams = queryParams;
    }

    @Override
    protected synchronized final ResultSet execute(URI baseApiUri, HttpHeaders baseHeaders)
            throws AlreadyExecutedException, StatementExecutionException {
        
        Map<String, Object> queryparams = new HashMap();
        if(entityIds!= null && entityIds.size() == 1) {
            queryparams.put("eid", entityIds.stream().findFirst());
        }
        if(entityIds == null || (entityIds != null && entityIds.size() > 2)) {
            queryparams.put("_limit", limit);
        }
        if(primaryKeyParams != null) {
            queryparams.putAll(primaryKeyParams);
        }
        if(expandRelations) {
            queryparams.put("_expandRelations", true);
        }
        setQueryparams(queryparams);
        
        setRequestMethod(HttpMethod.GET);
        return super.execute(baseApiUri, baseHeaders);
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
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        if(limit < 0 || limit > 1000) {
            throw new StatementException("Limit must be between 0 - 1000");
        }
        this.limit = limit;
        return this;
    }
    
    /**
     * When fetching data for {@link Resesource} also fetch all relation data.
     * @return this {@link GetStatement}
     * @see #reset() 
     */
    public GetStatement expandRelations() {
        this.expandRelations = true;
        return this;
    }
    
    /**
     * Sets the passed in entityId as entity id param for this {@link GetStatement}
     * and returns this {@link GetStatement}. If no id is passed in this method
     * is eqaul to get all entities. Pass in null to make this {@link GetStatement} get all
     * entities for the resource.
     * 
     * @param entityId the id of the entity to get.
     * @return a {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException
     */
    public GetStatement withId(UUID entityId) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        // This methid will be later removed and replaced by the out commenteds vithId(..) below ...
        if(entityId == null) {
            entityIds = null;
        } else {
            entityIds = Sets.newHashSet(entityId);
        }
        return this;
    }
    
    /**
     * Sets the passed in entityIds as entity id params for this {@link GetStatement}
     * and returns this {@link GetStatement}. Note that this methods adds ids
     * to the existing set of ids.
     * 
     * @param entityIds
     * @return a {@link GetStatement}
     * @see #reset() 
     */
//    public GetStatement withId(UUID... entityIds) throws AlreadyExecutedException {
//        
//        if(isExecuted()) {
//            throw new AlreadyExecutedException("Statement already executed.");
//        }
//        if(this.entityIds == null) {
//            this.entityIds = Sets.newHashSet(entityIds);
//        }
//        this.entityIds.addAll(Sets.newHashSet(entityIds));
//        return this;
//    }
    
    /**
     * Executes this {@link GetStatement} with passed in primary key attribute param.
     * Note that this method adds param to existing parameters.
     * 
     * @param name name of param
     * @param value value of param
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException
     * @throws StatementException if param is not a primary key attribute
     */
    public GetStatement withParam(String name, Object value) throws AlreadyExecutedException, StatementException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        if(!resource.getSettablePrimaryKeyNames().contains(name)) {
            throw new StatementException("Parameter is not valid in this GET statement."
                    + " Only use primary key attributes of current Resource");
        }
        
        if(this.primaryKeyParams == null) {
            this.primaryKeyParams = new HashMap();
        }
        this.primaryKeyParams.put(name, value);
        return this;
    }
    
    /**
     * Executes this {@link GetStatement} with passed in primary key attribute params.
     * Note that this method adds params to existing parameters.
     * 
     * @param params map of params
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException
     * @throws StatementException if any param is not a primary key attribute
     */
    public GetStatement withParams(Map<String, Object> params) throws AlreadyExecutedException, StatementException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        if(!params.keySet().stream().allMatch(param -> resource.getSettablePrimaryKeyNames().contains(param))) {
            throw new StatementException("Some params are not valid in this GET statement."
                    + " Only use primary key attributes of current Resource");
        }
        
        if(this.primaryKeyParams == null) {
            this.primaryKeyParams = new HashMap();
        }
        this.primaryKeyParams.putAll(params);
        return this;
    }

    /**
     * Resets this statement to an initial state as if you where creating a
     * new instance with the constructor {@link GetStatement#GetStatement(io.oopsie.sdk.model.Resource)}.
     * Note, this method calls super.reset() so any result this statement hold will be lost.
     * @see Statement#reset()
     */
    @Override
    public void reset() {
        super.reset();
        this.entityIds = null;
        this.expandRelations = false;
        this.limit = 300;
        this.primaryKeyParams = null;
    }
}
