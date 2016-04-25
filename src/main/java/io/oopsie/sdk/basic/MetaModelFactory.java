package io.oopsie.sdk.basic;

import io.oopsie.sdk.basic.model.ApplicationId;
import io.oopsie.sdk.basic.model.MetaModel;

/**
 * A factory contract for producing {@link MetaModel}
 * instances.
 */
public interface MetaModelFactory {

    /**
     * This method is used to create a {@link MetaModel}
     * instance from the provided application id argument.
     * @param applicationId the application id
     * @return the meta model for the provided application
     */
    MetaModel create(ApplicationId applicationId);
}
