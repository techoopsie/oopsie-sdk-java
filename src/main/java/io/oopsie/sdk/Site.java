package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.ApiURIException;
import io.oopsie.sdk.error.StatementExecutionException;
import io.oopsie.sdk.error.SiteCreationException;
import io.oopsie.sdk.error.IllegalIdentificationException;
import io.oopsie.sdk.error.SiteInitializationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * An instance of this class is the link between the client and the OOPSIE Cloud Site.
 * For the site object to connect call the {@link #init()} method.
 */
public class Site {
    
    private static final String URI_API_VERSION = "/api/v1";
    
    private ExecutorService cachedThreadPool;
    private String apiUrl;
    private URI apiUri;
    private UUID customerId;
    private UUID siteId;
    private String apiKey;
    private boolean initialized;
    private Applications applications;

    /**
     * Create a new {@link Site} object. to be able to use it you need
     * to call {@link #init()} first.
     * 
     * @param apiUri the api URI provided from your site settings.
     * @param customerId the customer id provided from your site settings.
     * @param siteId the site id provided from your site settings.
     * @throws SiteCreationException if not able to create Site
     */
    public Site(String apiUri, String customerId, String siteId) throws SiteCreationException {
        
        try {
            setApiUri(apiUri);
        } catch (ApiURIException ex) {
            throw new SiteCreationException(ex.getMessage());
        }
        
        try {
            setCustomerId(customerId);
            setSiteId(siteId);
        } catch (IllegalIdentificationException ex) {
            throw new SiteCreationException(ex.getMessage());
        }
    }
    
    /**
     * Create a new {@link Site} object. to be able to use it you need
     * to call {@link #init()} first.
     * 
     * @param apiUri the api URI provided from your site settings.
     * @param customerId the customer id provided from your site settings.
     * @param siteId the site id provided from your site settings.
     * @param apiKey the api key provided from your site settings.
     * @throws SiteCreationException if not able to create site
     */
    public Site(String apiUri, String customerId, String siteId, String apiKey) throws SiteCreationException {
        this(apiUri, customerId, siteId);
        this.apiKey = apiKey;
    }

    /**
     * Returns true if this {@link Site} object is ready to
     * execute api calls to specified site.
     * @return true if ready
     */
    public final boolean isInitialized() {
        return initialized;
    }

    /**
     * Returns the api URI for the remote REST service
     * @return an api URI
     */
    public final URI getApiUri() {
        return apiUri;
    }
    
    /**
     * Returns the api url set by user.
     * @return an api url
     */
    public final String getApiUrlString() {
        return apiUrl;
    }

    /**
     * Sets a new api URI. You must call {@link #init()} again to reinitialize
     * the {@link Site} object.
     * @param apiUri a new api URI
     * @throws ApiURIException if not able to parse the passed in URI string
     */
    public final void setApiUri(String apiUri)throws ApiURIException {
        try {
            this.apiUrl = apiUri;
            this.apiUri = new URI(apiUri + URI_API_VERSION);
        } catch (URISyntaxException ex) {
            throw new ApiURIException("Could not parse URI: Sure you passed in an"
                    + " api uri that is given to you from your site settings?");
        }
        this.initialized = false;
    }

    /**
     * Returns the customer id.
     * @return a customer id
     */
    public final UUID getCustomerId() {
        return customerId;
    }

    /**
     * Sets a new customer id. You must call {@link #init()} again to reinitialize
     * the {@link Site} object.
     * @param customerId a new api URI
     * @throws IllegalIdentificationException if not able to parse the passed in customer id string
     */
    public final void setCustomerId(String customerId) throws IllegalIdentificationException {
        try {
            this.customerId = UUID.fromString(customerId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalIdentificationException(
                    "Could not parse customerId: Sure you passed in a"
                    + " customerId that is given to you from your site settings?");
        }
        this.initialized = false;
    }

    /**
     * Returns the site id.
     * @return a site id
     */
    public final UUID getSiteId() {
        return siteId;
    }
    
    /**
     * Sets a new site id. You must call {@link #init()} again to reinitialize
     * the {@link Site} object.
     * @param siteId a new api URI
     * @throws IllegalIdentificationException if not able to parse the passed in site id string
     */
    public final void setSiteId(String siteId) throws IllegalIdentificationException {
        try {
            this.siteId = UUID.fromString(siteId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalIdentificationException(
                    "Could not parse siteId: Sure you passed in a"
                    + " siteId that is given to you from your site settings?");
        }
        this.initialized = false;
    }

    /**
     * Returns the api key currently used to authenticate {@link Statement} requests.
     * @return api key or null of not set
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Sets the api key to be used to authenticate {@link Statement} requests.
     * You must call {@link #init()} again to reinitialize the {@link Site} object.
     * @param apiKey the key
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.initialized = false;
    }
    
    /**
     * Initializes the {@link Site} object by calling init on the sites API
     * using the site information provided when the {@link Site} object was created
     * or set by any of its set methods.
     * 
     * @throws SiteInitializationException if method could not initialize the object.
     */
    public final void init() throws SiteInitializationException {
        cachedThreadPool = Executors.newWorkStealingPool();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("oopsie-customer-id", customerId.toString());
            headers.set("oopsie-site-id", siteId.toString());
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity httpEntity = new HttpEntity(headers);
            response = restTemplate.exchange(apiUri + "/init", HttpMethod.GET, httpEntity, Map.class);
        } catch(RestClientException ex) {
            throw new SiteInitializationException("Could not initialize OopsieSite object. "
                    + "Is your site deployed and/or is the passed in api URI correct?");
        }
        
        if(response.getStatusCodeValue() != HttpStatus.OK.value()) {
            throw new SiteInitializationException("Could not initialize OopsieSite object. "
            + response.getStatusCode().getReasonPhrase());
        }
        this.applications = InitParser.parse(response);
        this.initialized = true;
    }
    
    /**
     * Returns the {@link Application} related to the passed in name.
     * @param name tha anme of the application
     * @return the application
     */
    public final Application getApplication(String name) {
        
        return this.applications.getApplication(name);
    }

    /**
     * Returns all applications for this Site.
     * @return all apps
     */
    public final Applications getApplications() {
        return applications;
    }
    
    /**
     * Executes the passed in {@link Statement}. Any result returned can also be fetched by calling
     * {@link Statement#getResult() }. If execution fails due to
     * an error thrown from the remote site API and no {@link ResultSet} was 
     * produced then {@link Statement#isExecuted() } will return false.
     * 
     * @param statement the {@link Statement} to execute
     * @return the {@link ResultSet}
     * @throws AlreadyExecutedException if passed in {@link Statement} was previously executed
     * @throws StatementExecutionException if execution could not be fullfilled
     * @throws SiteInitializationException if not initialized properly
     */
    public final ResultSet execute(Statement statement) throws AlreadyExecutedException,
            StatementExecutionException, SiteInitializationException  {
        if(!initialized) {
            throw new SiteInitializationException("Site not initialized.");
        }
        return statement.execute(apiUri, customerId, siteId, apiKey, null);
    }
    
    /**
     * Executes the passed in {@link Statement}. Any result returned can also be fetched by calling
     * {@link Statement#getResult() }. If execution fails due to an error thrown from the
     * remote site API and no {@link ResultSet} was produced then {@link Statement#isExecuted() } will return false.
     * This method will prioritize the use of user auth cookies over the set api key. Pass in null
     * for the cookies param to use the set api key.
     * 
     * @param statement the {@link Statement} to execute
     * @param cookies the auth cookies
     * @return the {@link ResultSet}
     * @see #execute(io.oopsie.sdk.Statement)
     * @see #setApiKey(java.lang.String)
     * @see #login(io.oopsie.sdk.UserCredentials)
     * @throws AlreadyExecutedException if passed in {@link Statement} was previously executed
     * @throws StatementExecutionException if execution could not be fullfilled
     * @throws SiteInitializationException if not initialized properly
     */
    public final ResultSet execute(Statement statement, List<String> cookies) throws AlreadyExecutedException,
            StatementExecutionException, SiteInitializationException  {
        if(!initialized) {
            throw new SiteInitializationException("Site not initialized.");
        }
        return statement.execute(apiUri, customerId, siteId, apiKey, cookies);
    }
    
    /**
     * Executes passed in {@link Statement} asynchronously.
     * 
     * @param statement the {@link Statement} to execute
     * @return A {@link Future} {@link ResultSet}
     * @throws AlreadyExecutedException if passed in {@link Statement} was previously executed
     * @throws StatementExecutionException if execution could not be fullfilled
     * @throws SiteInitializationException if not initialized properly
     */
    public Future<ResultSet> executeAsync(Statement statement) throws AlreadyExecutedException,
            StatementExecutionException, SiteInitializationException {
        
        if(!initialized) {
            throw new SiteInitializationException("Site not initialized.");
        }
        
        if(statement.isUsingPageState()) {
            throw new SiteInitializationException("Asynchronous execution with page state not supported.");
        }
        return cachedThreadPool.submit(() -> statement.execute(apiUri, customerId, siteId,
                apiKey, null));
    }
    
    /**
     * Executes passed in {@link Statement} asynchronously.
     * This method will prioritize the use of user auth cookies over the set api key. Pass in null
     * for the cookies param to use the set api key.
     * 
     * @param statement the {@link Statement} to execute
     * @param cookies the auth cookies
     * @return A {@link Future} {@link ResultSet}
     * @see #executeAsync(io.oopsie.sdk.Statement) 
     * @see #setApiKey(java.lang.String)
     * @see #login(io.oopsie.sdk.UserCredentials)
     * @throws AlreadyExecutedException if passed in {@link Statement} was previously executed
     * @throws StatementExecutionException if execution could not be fullfilled
     * @throws SiteInitializationException if not initialized properly
     */
    public Future<ResultSet> executeAsync(Statement statement, List<String> cookies) throws AlreadyExecutedException,
            StatementExecutionException, SiteInitializationException {
        
        if(!initialized) {
            throw new SiteInitializationException("Site not initialized.");
        }
        
        if(statement.isUsingPageState()) {
            throw new SiteInitializationException("Asynchronous execution with page state not supported.");
        }
        return cachedThreadPool.submit(() -> statement.execute(apiUri, customerId, siteId,
                apiKey, cookies));
    }
    
    /**
     * Closing this {@link Site} and releases any resources this object holds gracefully.
     * To use the {@link Site} object again you need to call {@link #init()} again.
     * 
     * @param timeout the time
     * @param timeUnit the time unit
     * @return true if {@link Site} was gracefully closed, false if forced.
     * @see #close() 
     * 
     */
    public boolean close(long timeout, TimeUnit timeUnit) {
        
        boolean terminated = false;
        cachedThreadPool.shutdown();
        try {
            System.out.println("Shutting down ");
            terminated = cachedThreadPool.awaitTermination(timeout, timeUnit);
            if(!terminated) {
                // timed out, lets shutdown now!
                System.out.println("Graceful shutdown timed out, forcing termination.");
                cachedThreadPool.shutdownNow();
            }
        } catch(InterruptedException e) {
            // problems? ... shutdown now!
            System.out.println("Error while waiting for graceful shutdown, forcing termination.");
            cachedThreadPool.shutdownNow();
        }
        return terminated;
    }
    
    /**
     * Close this {@link Site} immediately. Warning! This methids will not wait
     * for any running executions to finnish.
     * To use the {@link Site} object again you need to call {@link #init()} again.
     * @see #close(long, java.util.concurrent.TimeUnit) 
     */
    public void close() {
        
        cachedThreadPool.shutdownNow();
    }
    
    /**
     * Register a new user in this Site. Upon successful registration this
     * method will return a UserCredential instance.
     * 
     * @param userRequest user request info.
     * @return user credentials.
     * @throws StatementExecutionException if registration failed.
     * @see #login(io.oopsie.sdk.UserCredentials) 
     */
    public UserCredentials register(UserRequest userRequest) throws StatementExecutionException {
        String regURI = String.join("", apiUri.toString(), "/users/register");
        
        HttpHeaders regHeaders = new HttpHeaders();
        regHeaders.set("oopsie-customer-id", customerId.toString());
        regHeaders.set("oopsie-site-id", siteId.toString());
        
        HttpEntity regEntity =  new HttpEntity(userRequest, regHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            
            response = restTemplate.exchange(
                    regURI,
                    HttpMethod.POST,
                    regEntity,
                    String.class);
            
        } catch(Exception ex) {
            if(ex instanceof HttpClientErrorException) {
                String body = ((HttpClientErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpClientErrorException) ex).getMessage() + ", " + body);
            } else {
                throw new StatementExecutionException("Severe: " + ex.getMessage());
            }
        }
        return new UserCredentials(userRequest.getEmail(), userRequest.getPassword());
    }
    
    /**
     * Logging in using passed in {@link UserCredentials}. The returned array
     * of cookies is in order (1) the access token cookie, (2) the refresh
     * token cookie.
     * @param user the user to login
     * @return the user auth cookies
     * @throws StatementExecutionException if login failed.
     */
    public List<String> login(UserCredentials user) throws StatementExecutionException {
        
        String loginURI = String.join("",
                apiUri.toString(),
                "/users/login");
        
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.set("oopsie-customer-id", customerId.toString());
        loginHeaders.set("oopsie-site-id", siteId.toString());
        
        HttpEntity loginEntity =  new HttpEntity(user, loginHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            
            response = restTemplate.exchange(
                    loginURI,
                    HttpMethod.POST,
                    loginEntity,
                    String.class);
            
        } catch(Exception ex) {
            if(ex instanceof HttpClientErrorException) {
                String body = ((HttpClientErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpClientErrorException) ex).getMessage() + ", " + body);
            } else {
                throw new StatementExecutionException("Severe: " + ex.getMessage());
            }
        }
        
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        return cookies;
    }
    
    /**
     * Logging out current user session.
     * 
     * @param cookies the user auth cookies
     * @see #login(io.oopsie.sdk.UserCredentials) 
     * @throws StatementExecutionException if logout failed.
     */
    public void logout(List<String> cookies) throws StatementExecutionException {
        String loginURI = String.join("",
                apiUri.toString(),
                "/users/logout");
        
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.set("oopsie-customer-id", customerId.toString());
        logoutHeaders.set("oopsie-site-id", siteId.toString());
        logoutHeaders.add("Cookie", cookies.get(0));
        logoutHeaders.add("Cookie", cookies.get(1));
        
        HttpEntity logoutEntity =  new HttpEntity(null, logoutHeaders);
        RestTemplate restTemplate = new RestTemplate();
        try {
            
            restTemplate.exchange(
                    loginURI,
                    HttpMethod.POST,
                    logoutEntity,
                    String.class);
            
        } catch(Exception ex) {
            if(ex instanceof HttpClientErrorException) {
                String body = ((HttpClientErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpClientErrorException) ex).getMessage() + ", " + body);
            } else {
                throw new StatementExecutionException("Severe: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Refreshes an expired auth token cookies.
     * @param cookies the auth token cookies
     * @return the refreshed auth token cookies.
     * @see #login(io.oopsie.sdk.UserCredentials) 
     * @throws StatementExecutionException thrown if execution failed
     */
    public List<String> refresh(List<String> cookies) throws StatementExecutionException {
        String loginURI = String.join("",
                apiUri.toString(),
                "/users/refresh");
        
        HttpHeaders refreshHeaders = new HttpHeaders();
        refreshHeaders.set("oopsie-customer-id", customerId.toString());
        refreshHeaders.set("oopsie-site-id", siteId.toString());
        refreshHeaders.add("Cookie", cookies.get(1));
        
        HttpEntity refreshEntity =  new HttpEntity(null, refreshHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            
            response = restTemplate.exchange(
                    loginURI,
                    HttpMethod.POST,
                    refreshEntity,
                    String.class);
            
        } catch(Exception ex) {
            if(ex instanceof HttpClientErrorException) {
                String body = ((HttpClientErrorException)ex).getResponseBodyAsString();
                throw new StatementExecutionException(((HttpClientErrorException) ex).getMessage() + ", " + body);
            } else {
                throw new StatementExecutionException("Severe: " + ex.getMessage());
            }
        }
        
        List<String> refreshedCookies = response.getHeaders().get("Set-Cookie");
        return refreshedCookies;
    }
}
