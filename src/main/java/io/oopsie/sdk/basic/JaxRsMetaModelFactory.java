package io.oopsie.sdk.basic;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.oopsie.sdk.basic.model.ApplicationId;
import io.oopsie.sdk.basic.model.MetaModel;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import static java.util.Objects.requireNonNull;

/**
 * This is a JAX-RS implementation of the meta model factory
 * contract. When calling the {@link #create(ApplicationId)}
 * method, a call to the enclosed URL will be made to fetch
 * the meta model for the provided application id. The response
 * will be mapped to a {@link MetaModel} instance via Jackson.
 *
 * Further on, this class (or its future collaborators) will most
 * probably include mechanisms for handling credentials, tokens,
 * exception management etc.
 */
public final class JaxRsMetaModelFactory implements MetaModelFactory {

    private final String url;

    /**
     * Creates a new model factory instance that will target
     * the provided URL.
     * @param url the URL of the remote meta model resource
     */
    public JaxRsMetaModelFactory(String url) {
        this.url = requireNonNull(url);
    }

    @Override
    public MetaModel create(ApplicationId applicationId) {

        Client client = ClientBuilder.newClient().register(JacksonJsonProvider.class);

        return client.target(url).request(MediaType.APPLICATION_JSON_TYPE).get().readEntity(MetaModel.class);
    }
}
