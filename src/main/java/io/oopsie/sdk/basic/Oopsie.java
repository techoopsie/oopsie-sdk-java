package io.oopsie.sdk.basic;

import io.oopsie.sdk.basic.model.ApplicationId;
import io.oopsie.sdk.basic.model.MetaModel;

import static java.util.Objects.requireNonNull;

/**
 * This is the main entry point for SDK users. Right
 * now it does not contain much.
 */
public final class Oopsie {

    private final MetaModel model;

    Oopsie(MetaModel model) {
        this.model = MetaModel.Validator.validate(requireNonNull(model));
    }

    /**
     * Static factory for creating a new Oopsie instance.
     *
     * @param id The application id
     * @return a fresh Oopsie instance tied to a particular application
     */
    public static Oopsie create(ApplicationId id) {
        return new Oopsie(new JaxRsMetaModelFactory("url goes here..").create(id));
    }
}
