package io.oopsie.sdk.basic.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The meta model and its friends are created via
 * reflection from the JSON deserialization library.
 * Hence, it is not possible to create a meta model
 * as it is always constructed from the result of
 * an API call.
 */
public final class MetaModel {
    private CustomerId customerId;
    private ApplicationId applicationId;
    private List<ResourceMeta> resourceMetas;

    private MetaModel() {}

    /**
     * This method asserts the structure of the present
     * meta model. Normally, the state of an object should
     * be validated during its construction. The problem is
     * that the JSON deserialization library instantiates
     * the meta model automatically, and I am still to find
     * a way to put constraints on the instances it creates.
     * This will have to do until we find a better solution.
     *
     * @throws NullPointerException if any required fields
     * in the object are null
     */
    public MetaModel assertValid() {
        return Validator.validate(this);
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public ApplicationId getApplicationId() {
        return applicationId;
    }

    public List<ResourceMeta> getResourceMetas() {
        return Collections.unmodifiableList(resourceMetas);
    }

    public void setCustomerId(CustomerId customerId) {
        this.customerId = customerId;
    }
    private void setApplicationId(ApplicationId applicationId) {
        this.applicationId = applicationId;
    }
    private void setResourceMetas(List<ResourceMeta> resourceMetas) {
        this.resourceMetas = resourceMetas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaModel metaModel = (MetaModel) o;
        return Objects.equals(customerId, metaModel.customerId) &&
                Objects.equals(applicationId, metaModel.applicationId) &&
                Objects.equals(resourceMetas, metaModel.resourceMetas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, applicationId, resourceMetas);
    }

    @Override
    public String toString() {
        return "MetaModel{" +
                "customerId=" + customerId +
                ", applicationId=" + applicationId +
                ", resourceMetas=" + resourceMetas +
                '}';
    }

    /**
     * As the meta model is constructed via reflection from
     * the JSON deserialization library, there is no apparent
     */
    public static class Validator {
        public static MetaModel validate(MetaModel model) {
            requireNonNull(model.getCustomerId(), "customerId was null");
            requireNonNull(model.getApplicationId(), "applicationId was null");
            requireNonNull(model.getResourceMetas(), "resourceMetas was null");

            model.getResourceMetas().stream()
                    .forEach(rm -> requireNonNull(rm.getResourceId(),
                            String.format("resourceId for %s was null", rm)));

            model.getResourceMetas().stream()
                    .forEach(rm -> requireNonNull(rm.getAttributeMetas(),
                            String.format("attributeMetas for %s was null", rm)));

            model.getResourceMetas().stream()
                    .forEach(rm -> rm.getAttributeMetas()
                            .forEach(am -> {
                                requireNonNull(am.getName(), String.format("name for %s was null", am));
                                requireNonNull(am.getType(), String.format("name for %s was null", am));
                            }));

            return model;
        }
    }
}
