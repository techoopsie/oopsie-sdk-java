package io.oopsie.sdk.basic.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The resource id.
 */
public final class ResourceId {
    private final String value;

    private ResourceId(String value) {
        this.value = requireNonNull(value);
    }

    public static ResourceId of(String value) {
        return new ResourceId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceId that = (ResourceId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ResourceId{" +
                "value='" + value + '\'' +
                '}';
    }
}
