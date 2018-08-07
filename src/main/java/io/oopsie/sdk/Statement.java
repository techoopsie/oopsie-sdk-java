package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementExecutionException;
import io.oopsie.sdk.error.NotExecutedException;
import io.oopsie.sdk.error.StatementParamException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A Statement is the executional definition for a specific {@link Resource} 
 * @param <T> sub type
 */
public abstract class Statement<T extends Statement> {
    
    private Object requestBody;
    private Map<String, Object> queryparams; 
    protected boolean executed;
    private ResultSet result;
    protected String pageState;
    
    // these fields are never called from reset method
    protected final Resource resource;
    private Set<String> statementParams;
    private String view;
    private HttpMethod requestMethod;

    /**
     * Creates a new Statement for specified {@link Resource}.
     * @param resource a resource
     */
    Statement(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * Adds and/or replaces passed in param.
     * 
     * @param name name of param
     * @param value value of param
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException if executed
     * @throws StatementParamException if param is not part of this resource
     */
    public T withParam(String name, Object value) throws AlreadyExecutedException, StatementParamException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        validateParamName(name);
        if(this.queryparams == null) {
            this.queryparams = new HashMap();
        }
        this.queryparams.put(name, value);
        return (T)this;
    }
    
    /**
     * Adds and/or replaces passed in params.
     * 
     * @param params map of params
     * @return this {@link T}
     * @see #reset() 
     * @throws AlreadyExecutedException if executed
     * @throws StatementParamException if any param is not part of this resource
     */
    public T withParams(Map<String, Object> params) throws AlreadyExecutedException, StatementParamException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        params.keySet().forEach(param -> validateParamName(param));
        if(this.queryparams == null) {
            this.queryparams = new HashMap();
        }
        this.queryparams.putAll(params);
        return (T)this;
    }
    
    private void validateParamName(String name) throws StatementParamException {
        
        Set<String> allParams = new HashSet();
        allParams.addAll(resource.getAttributeNames());
        if(statementParams != null) {
            allParams.addAll(statementParams);
        }
                
        if(!allParams.contains(
                name.substring(0, (name.contains("[") ? name.indexOf("[") : name.length())))) {
            throw new StatementParamException("Param '" + name + "' is not part of this resource."
                    + " Only use attributes of current resource and statement.");
        }
    }
    
    protected void setView(String view) {
        this.view = view;
    }

    /**
     * Set any sub statement params that can be
     * used when calling {@link #withParam(java.lang.String, java.lang.Object) }
     * and {@link #withParams(java.util.Map) }
     * @param statementParams set of param names
     */
    protected void setStatementParams(Set<String> statementParams) {
        this.statementParams = statementParams;
    }
    
    /**
     * Returns all statement params set by sub statements that can be
     * used when calling {@link #withParam(java.lang.String, java.lang.Object) }
     * and {@link #withParams(java.util.Map) }
     * @return all sub statement params
     */
    protected Set<String> getStatementParams() {
        return statementParams;
    }
 
    /**
     * Returns true if statement's execution will inlcude "pageState" query param.
     * @return true if using pageState
     */
    final boolean isUsingPageState() {
        return queryparams != null && queryparams.containsKey("pageState");
    }
    
    /**
     * Ruturns true if this {@link Statement} is executed.
     * @return executed state
     */
    public final boolean isExecuted() {
        return this.executed;
    }
    
    /**
     * Returns the {@link ResultSet} for the executed {@link Statement}.
     * @return the {@link ResultSet}
     * @throws NotExecutedException if not executed
     */
    public final ResultSet getResult() throws NotExecutedException {
        if(!isExecuted()) {
            throw new NotExecutedException("Statement not yet executed");
        }
        return result;
    }
    
    /**
     * Resets this statement to its initial state when instance was created so this
     * {@link Statement} can be reused and executed once more. Note, any result
     * that this {@link Statement} holds will be lost.
     */
    public void reset() {
        this.queryparams = null;
        this.result = null;
        this.requestBody = null;
        this.executed = false;
        this.pageState = null;
        // resource should not be reset
        // requestMethod should not be reset
        // statementParams should not be reset
        // view should not be reset
    }
    
    /**
     * Returns the target resource.
     * @return {@link Resource}
     */
    public Resource getResource() {
        return resource;
    }

    protected final HttpMethod getRequestMethod() {
        return requestMethod;
    }

    protected final void setRequestMethod(HttpMethod requestMethod) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        this.requestMethod = requestMethod;
    }

    protected Object getRequestBody() {
        return requestBody;
    }

    protected final void setRequestBody(Object requestBody) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        this.requestBody = requestBody;
    }

    protected final Map<String, Object> getQueryparams() {
        return queryparams;
    }

    protected final void setQueryparams(Map<String, Object> queryparams) throws AlreadyExecutedException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        this.queryparams = queryparams;
    }
    
    protected ResultSet execute(URI requestBaseApiUri, UUID customerId, UUID siteId,
            String apiKey, List<String> cookies)
            throws AlreadyExecutedException, StatementExecutionException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        String viewPart = "";
        if(resource.getViewNames().contains(view)) {
            viewPart = "/views/" + view;
        }
        String baseURI = String.join("",
                requestBaseApiUri.toString(),
                "/resources/",
                resource.getResourceId().toString(),
                viewPart
                );
        
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseURI);
        if(queryparams != null && !queryparams.isEmpty()) {
            queryparams.forEach((k,v) -> {
                Object val = v;
                if(val instanceof Date) {
                    val = ((Date)val).toInstant();
                }
                uriBuilder.queryParam(k, val);
            });
        }
        URI requestUri = uriBuilder.build().encode().toUri();        
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("oopsie-customer-id", customerId.toString());
        headers.set("oopsie-site-id", siteId.toString());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        // Prioritize user cookies API is looking at API KEY first.
        // If this SDK is used with user login then we want user auth to be used.
        if(cookies != null) {
            headers.add("Cookie", cookies.get(0));
            headers.add("Cookie", cookies.get(1));
        } else {
            headers.set("Authorization", apiKey);
        }
        HttpEntity httpEntity =  new HttpEntity(requestBody, headers);
        
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        
        try {
            
            response = restTemplate.exchange(
                    requestUri,
                    requestMethod,
                    httpEntity,
                    Map.class);
            
            
        } catch(Exception ex) {
            if(ex instanceof HttpClientErrorException) {
                String body = ((HttpClientErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpClientErrorException) ex).getMessage() + ", " + body);
            } else if(ex instanceof HttpServerErrorException) {
                String body = ((HttpServerErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpServerErrorException) ex).getMessage() + ", " + body);
            } else {
                throw new StatementExecutionException("Severe: " + ex.getMessage());
            }
        }
        
        Map<String, Object> responseBody = (Map)response.getBody();
        if(getRequestMethod().equals(HttpMethod.GET)) {
            List entities = (List)responseBody.get("entities");
            this.result = new ResultSet(this, true, entities);
        } else {
            List data = new ArrayList();
            data.add(responseBody);
            this.result = new ResultSet(this, true, data);
        }
        if(responseBody != null && responseBody.get("metadata") != null) {
            Map<String, Object> metaData = (Map)responseBody.get("metadata");
            pageState = metaData.get("pageState") != null ? metaData.get("pageState").toString() : null;
        }
        executed = true;
        return result;
    }
}
