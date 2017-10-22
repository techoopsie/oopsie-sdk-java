package io.oopsie.sdk;

import io.oopsie.sdk.error.NotFoundInModelException;
import java.util.Map;

/**
 * Holds all applications for a {@link Site}.
 */
public class Applications {
    
    private final Map<String, Application> applications;

    /**
     * Creates an new Applications.
     * @param applications the applications
     */
    Applications(Map<String, Application> applications) {
        this.applications = applications;
    }
    
    /**
     * Returns the named {@link Application}.
     * @param name the application name
     * @return an application
     * @throws NotFoundInModelException if application not found
     */
    public final Application getApplication(String name) throws NotFoundInModelException {
        if(!applications.containsKey(name)) {
            throw new NotFoundInModelException("'" + name + "' not part of this site model.");
        }
        return this.applications.get(name);
    }
}
