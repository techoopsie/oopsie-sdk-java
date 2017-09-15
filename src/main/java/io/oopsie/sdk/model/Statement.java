package io.oopsie.sdk.model;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementExecutionException;
import io.oopsie.sdk.error.NotExecutedException;
import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public abstract class Statement {
    
    protected final Resource resource;
    private HttpMethod requestMethod;
    private Object requestBody;
    private Map<String, Object> queryparams; 
    private boolean executed;
    private Result result;

    Statement(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * Ruturns true if this {@link Statement} is executed.
     * @return executed state
     */
    public final boolean isExecuted() {
        return this.executed;
    }
    
    /**
     * Returns the {@link Result} for the executed {@link Statement}.
     * @return the {@link Result}
     * @throws NotExecutedException 
     */
    public final Result getResult() throws NotExecutedException {
        if(!isExecuted()) {
            throw new NotExecutedException("Statement not yet executed");
        }
        return result;
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
    
    protected Result execute(URI requestBaseApiUri, HttpHeaders baseHeaders)
            throws AlreadyExecutedException, StatementExecutionException {
        
        if(isExecuted()) {
            throw new AlreadyExecutedException("Statement already executed.");
        }
        
        String baseURI = String.join("",
                requestBaseApiUri.toString(),
                "/resources/",
                resource.getResourceId().toString()
                );
        
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(baseURI);
        if(queryparams != null && !queryparams.isEmpty()) {
            queryparams.forEach((k,v) -> uriBuilder.queryParam(k, v));
        }
        URI requestUri = uriBuilder.build().encode().toUri();
        
        HttpEntity httpEntity =  new HttpEntity(requestBody, baseHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            
            response = restTemplate.exchange(
                    requestUri,
                    requestMethod,
                    httpEntity,
                    Object.class);
            
        } catch(RestClientException ex) {
            if(ex instanceof HttpClientErrorException) {
                throw new StatementExecutionException(((HttpClientErrorException)ex).getResponseBodyAsString());
            }
        }
        
        Object responseBody = response.getBody();
        
        // include all 200's as accepted
        if(response.getStatusCodeValue() > 299) {
            throw new StatementExecutionException("Could not initialize OopsieSite object. "
            + response.getStatusCode().getReasonPhrase());
        }
        this.result = new Result(this, true);
        this.executed = true;
        return this.result;
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
        this.requestMethod = null;
        this.executed = false;
    }
}
