package io.oopsie.sdk.model;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementException;
import io.oopsie.sdk.error.StatementExecutionException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class GetStatement extends Statement {
    
    private int limit = 300;
    private UUID[] entityIds;
    private Map<String, Object> primaryKeyParams;
    
    
    GetStatement(Resource resource) {
        super(resource);
    }
    
    GetStatement(Resource resource, UUID... entityIds) {
        super(resource);
        this.entityIds = entityIds;
    }
    
    GetStatement(Resource resource, Map<String, Object> queryParams, UUID... entityIds) {
        super(resource);
        this.entityIds = entityIds;
        this.primaryKeyParams = queryParams;
    }

    @Override
    protected synchronized final Result execute(URI baseApiUri, HttpHeaders baseHeaders)
            throws AlreadyExecutedException, StatementExecutionException {
        
        Map<String, Object> queryparams = new HashMap();
        if(entityIds!= null && entityIds.length == 1) {
            queryparams.put("eid", entityIds[0]);
        }
        if(entityIds== null || (entityIds !=null && entityIds.length > 2)) {
            queryparams.put("limit", limit);
        }
        if(primaryKeyParams != null) {
            queryparams.putAll(primaryKeyParams);
        }
        setQueryparams(queryparams);
        
        setRequestMethod(HttpMethod.GET);
        return super.execute(baseApiUri, baseHeaders);
    }
    
    /**
     * Sets a new fetch size limit. Default is 300.
     * @param limit
     * @return this {@link GetStatement}
     * @see #reset() 
     */
    public final GetStatement limit(int limit) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        this.limit = limit;
        return this;
    }
    
    /**
     * Sets the passed in entityId as query params for this {@link GetStatement}
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
        UUID[] id = {entityId};
        this.entityIds = id;
        return this;
    }
    
    /**
     * Executes this {@link GetStatement} with passed in primary key attribute params.
     * Note taht this method adds param to existing parameters.
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
     * Note taht this method adds params to existing parameters.
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
     * Sets the passed in entityIds as query params for this {@link GetStatement}
     * and returns this {@link GetStatement}. If no ids are apssed in this method
     * is eqaul to get all entities.
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
//        this.entityIds = entityIds;
//        return this;
//    }
}
