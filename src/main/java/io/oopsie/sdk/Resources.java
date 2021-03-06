package io.oopsie.sdk;

import io.oopsie.sdk.error.NotFoundInModelException;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Holds all resources for an {@link Application}
 */
public class Resources {
    
    private final Map<String, Resource> resources;

    /**
     * Creates new Resources.
     * @param resources the reosurces
     */
    Resources(Map<String, Resource> resources) {
        this.resources = resources;
    }
    
    /**
     * Returns a named resource..
     * @return a resource
     * @param name name of resource
     */
    public final Resource getResource(String name) throws NotFoundInModelException {
        if(!resources.containsKey(name)) {
            throw new NotFoundInModelException("'" + name + "' not part of this site model.");
        }
        return this.resources.get(name);
    }
    
    /**
     * Stream representation of this Resources object.
     * @return a stream of resources.
     */
    public final Stream<Resource> stream() {
        return resources.values().stream();
    }
}
