package io.oopsie.sdk.basic.model;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represents meta information of a
 * particular resource type in Oopsie. Resource
 * meta instances are created via reflection from
 * the JSON deserialization library. Hence, it
 * is neither possible or meaningful to create
 * new objects of this type.
 */
public class ResourceMeta {
    private ResourceId resourceId;
    private List<AttributeMeta> attributeMetas;

    private ResourceMeta() {}

    public ResourceId getResourceId() {
        return resourceId;
    }

    public List<AttributeMeta> getAttributeMetas() {
        return Collections.unmodifiableList(attributeMetas);
    }

    private void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    private void setAttributeMetas(List<AttributeMeta> attributeMetas) {
        this.attributeMetas = attributeMetas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceMeta that = (ResourceMeta) o;
        return Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(attributeMetas, that.attributeMetas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId, attributeMetas);
    }

    @Override
    public String toString() {
        return "ResourceMeta{" +
                "resourceId=" + resourceId +
                ", attributeMetas=" + attributeMetas +
                '}';
    }
}
