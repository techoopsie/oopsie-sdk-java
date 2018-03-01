package io.oopsie.sdk;

/**
 * Validation constraints for data values.
 */
public class Validation {
    
    private final long min;
    private final long max;

    Validation(long min, long max) {
        this.min = min;
        this.max = max;
    }

    /**
     * The minimum value of the data value.
     * @return the minimum constraint
     */
    public final long getMin() {
        return min;
    }

    /**
     * The maximium value of the data value. If 0 is returned no limit
     * has been set.
     * @return the maximum constraint
     */
    public final long getMax() {
        return max;
    }
}
