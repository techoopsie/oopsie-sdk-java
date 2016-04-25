package io.oopsie.sdk.basic.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The customer id.
 */
public final class CustomerId {
    private final String value;

    private CustomerId(String value) {
        this.value = requireNonNull(value);
    }

    public static CustomerId of(String value) {
        return new CustomerId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CustomerId{" +
                "value='" + value + '\'' +
                '}';
    }
}
