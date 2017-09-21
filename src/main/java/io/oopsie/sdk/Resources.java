package io.oopsie.sdk;

import io.oopsie.sdk.error.ModelException;
import java.util.Map;

public class Resources {
    
    private final Map<String, Resource> resources;

    Resources(Map<String, Resource> resources) {
        this.resources = resources;
    }
    
    /**
     * Returns a named resource..
     * @return a resource
     */
    public final Resource getResource(String name) throws ModelException {
        if(!resources.containsKey(name)) {
            throw new ModelException("'" + name + "' not part of this site model.");
        }
        return this.resources.get(name);
    }
}
