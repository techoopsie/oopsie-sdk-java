package io.oopsie.sdk;

import io.oopsie.sdk.error.NotFoundInModelException;
import java.util.stream.Stream;

public class Application {
    
    private final String name;
    private final Resources resources;

    Application(String name, Resources resources) {
        this.name = name;
        this.resources = resources;
    }

    /**
     * Returns the name of the Application.
     * @return app name.
     */
    public String getName() {
        return name;
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

    /**
     * Returns the Resources object for this Application
     * @return Resources object.
     */
    public Resources getResources() {
        return resources;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
