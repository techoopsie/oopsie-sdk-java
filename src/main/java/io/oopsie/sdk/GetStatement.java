package io.oopsie.sdk;

import io.oopsie.sdk.error.AlreadyExecutedException;
import io.oopsie.sdk.error.StatementExecutionException;
import io.oopsie.sdk.error.StatementParamException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.http.HttpMethod;

/**
 * Use this class to fetch (HTTP GET) entities stored in a remote OOPSIE cloud for a
 * specific {@link Resource}.
 */
public class GetStatement extends Statement<GetStatement> {
    
    /**
     * Creates a new GetStatement for specified {@link Resource}.
     * @param resource the resource
     */
    GetStatement(Resource resource) {
        this(resource, resource.getPrimaryViewName());
    }
    
    /**
     * Creates a new GetStatement for specified {@link Resource}.
     * @param resource the resource
     */
    GetStatement(Resource resource, String view) {
        super(resource);
        if(!resource.getViewNames().contains(view)) {
            throw new IllegalArgumentException("'" + view + "'" + " is not a view in the passed in resource '" + resource.getName() + "'");
        }
        setRequestMethod(HttpMethod.GET);
        Set<String> statementParams = new HashSet();
        statementParams.add("_limit");
        statementParams.add("pageState");
        setStatementParams(statementParams);
        setView(view);
    }
    
    /**
     * Returns next page of this statement. The result of the last page is empty.
     * @return the next page.
     */
    public final GetStatement nextPage() {
        
        if(getPageState() == null) {
            throw new StatementExecutionException("No more pages to fetch");
        }
        return page(pageState);
    }
    
    /**
     * Fetches a certain page by passing in a pageState. Passing in null is the same as
     * getting the first page. The result of the last page is empty.
     * @param pageState a page state
     * @return this statement
     */
    public final GetStatement page(String pageState) {
        executed = false;
        return withParam("pageState", pageState);
    }
    
    /**
     * Returns true if this GetStatement has more pages to fetch.
     * @return true if statement has more pages to fetch.
     */
    public final boolean hasMorePages() {
        return pageState != null;
    }
    
    /**
     * The page state of the statment. If not null use this to fetch next page
     * or store it to fetch the page for later use. Might return null, which
     * means there are no more pages to fetch.
     * 
     * @see #page(java.lang.String) 
     * @see #nextPage() 
     * @return current pageState or null
     */
    public final String getPageState() {
        return pageState;
    }

    /**
     * Set a new fetch size limit between 0 through 1000. Default is 300. 
     * @param limit the limit
     * @return this {@link GetStatement}
     * @see #reset() 
     * @throws AlreadyExecutedException if executed
     * @throws StatementParamException if limit is out of range (0-1000)
     */
    public final GetStatement limit(int limit) throws AlreadyExecutedException, StatementParamException {
        
        if(limit < 0 || limit > 1000) {
            throw new StatementParamException("Limit must be between 0 - 1000");
        }
        return withParam("_limit", limit);
    }
}
