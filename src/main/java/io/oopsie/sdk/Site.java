package io.oopsie.sdk;

import io.oopsie.sdk.error.UserException;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class Site {
    
    private static final String URI_API_VERSION = "/api/v1";
    
    private ExecutorService cachedThreadPool;
    private URI apiUri;
    private UUID customerId;
    private UUID siteId;
    private String apiKey;
    private String authCookie;
    private String refreshAuthCookie;
    //private HttpHeaders headers;
    //private HttpEntity<String> httpEntity;
    private boolean initialized;
    private Applications applications;

    /**
     * Create a new {@link OopsieSite} object. to be able to use it you need
     * to call {@link #init()} first.
     * 
     * @param apiUri the api URI provided from your site settings.
     * @param customerId the customer id provided from your site settings.
     * @param siteId the site id provided from your site settings.
     * @throws SiteCreationException 
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
     * Create a new {@link OopsieSite} object. to be able to use it you need
     * to call {@link #init()} first.
     * 
     * @param apiUri the api URI provided from your site settings.
     * @param customerId the customer id provided from your site settings.
     * @param siteId the site id provided from your site settings.
     * @param apiKey the api key provided from your site settings.
     * @throws SiteCreationException 
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
     * Sets a new api URI. You must call {@link #init()} again to reinitialize
     * the {@link Site} object.
     * @param apiUri a new api URI
     * @throws ApiURIException if not able to parse the passed in URI string
     */
    public final void setApiUri(String apiUri)throws ApiURIException {
        try {
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
     * Executes the passed in {@link Statement}. 
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
        return statement.execute(apiUri, customerId, siteId, apiKey, authCookie);
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
        return cachedThreadPool.submit(() -> statement.execute(apiUri, customerId, siteId, apiKey, authCookie));
    }
    
    /**
     * Closing this {@link Site} and releases any resources this object holds gracefully.
     * To use the {@link Site} object again you need to call {@link #init()} again.
     * 
     * @param timeout
     * @param timeUnit
     * @return true if {@link Site} was gracefully close, false if forced.
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
     * Logging in using passed in {@link User}.
     * @param user the user to login 
     */
    public void login(User user) throws UserException {
        
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
            throw new UserException("Severe: " + ex.getMessage());
        }
        
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        this.authCookie= cookies.get(0);
        this.refreshAuthCookie = cookies.get(1);
    }
    
    /**
     * Logging out current user session.
     */
    public void logout() throws StatementExecutionException {
        String loginURI = String.join("",
                apiUri.toString(),
                "/users/logout");
        
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.set("oopsie-customer-id", customerId.toString());
        logoutHeaders.set("oopsie-site-id", siteId.toString());
        logoutHeaders.add("Cookie", authCookie);
        
        HttpEntity logoutEntity =  new HttpEntity(null, logoutHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity response = null;
        try {
            
            response = restTemplate.exchange(
                    loginURI,
                    HttpMethod.POST,
                    logoutEntity,
                    String.class);
            
        } catch(Exception ex) {
            throw new UserException("Severe: " + ex.getMessage());
        }
        this.authCookie = null;
        this.refreshAuthCookie = null;
    }
    
    /**
     * Close this {@link Site} immediately. Warning! This methids will not wait
     * for any running executions to finnish.
     * To use the {@link Site} object again you need to call {@link #init()} again.
     */
    public void close() {
        
        cachedThreadPool.shutdownNow();
    }
}
