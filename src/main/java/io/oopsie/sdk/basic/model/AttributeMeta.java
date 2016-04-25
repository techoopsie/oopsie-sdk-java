package io.oopsie.sdk.basic.model;

import java.util.Objects;

/**
 * This class holds attribute metadata for a
 * particular {@link ResourceMeta} in the meta model.
 * Attribute meta instances are created via
 * reflection from the JSON deserialization library.
 * Hence, it is neither possible or meaningful to
 * create new objects of this type.
 */
public final class AttributeMeta {
    private AttributeName name;
    private AttributeType type;

    private AttributeMeta() {}

    public AttributeName getName() {
        return name;
    }

    public AttributeType getType() {
        return type;
    }

    private void setName(AttributeName name) {
        this.name = name;
    }
    private void setType(AttributeType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeMeta that = (AttributeMeta) o;
        return Objects.equals(name, that.name) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return "AttributeMeta{" +
                "name=" + name +
                ", type=" + type +
                '}';
    }
}
