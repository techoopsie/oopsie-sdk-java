package io.oopsie.sdk;

import io.oopsie.sdk.error.ModelException;
import java.util.Map;

public class Applications {
    
    private final Map<String, Application> applications;

    Applications(Map<String, Application> applications) {
        this.applications = applications;
    }
    
    /**
     * Returns the named {@link Application}.
     * @param name
     * @return an application
     * @throws ModelException 
     */
    public final Application getApplication(String name) throws ModelException {
        if(!applications.containsKey(name)) {
            throw new ModelException("'" + name + "' not part of this site model.");
        }
        return this.applications.get(name);
    }
}
