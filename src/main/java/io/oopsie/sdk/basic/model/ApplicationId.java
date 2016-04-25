package io.oopsie.sdk.basic.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * The application identity.
 */
public final class ApplicationId {
    private final String value;

    private ApplicationId(String value) {
        this.value = requireNonNull(value);
    }

    public static ApplicationId of(String value) {
        return new ApplicationId(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationId that = (ApplicationId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ApplicationId{" +
                "value='" + value + '\'' +
                '}';
    }
}
