package io.oopsie.sdk;

import io.oopsie.sdk.error.NotFoundInModelException;

public class Application {
    
    private final Resources resources;

    Application(Resources resources) {
        this.resources = resources;
    }
    
    /**
     * Returns the named {@link Resource} from related {@link Site} object.
     * 
     * @param name the name of the resource to get
     * @return the named {@link Resource}
     * @throws NotFoundInModelException if name is not part of the model
     */
    public final Resource getResource(String name) throws NotFoundInModelException {
        return this.resources.getResource(name);
    }
}
