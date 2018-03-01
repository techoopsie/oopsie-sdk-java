package io.oopsie.sdk;

/**
 * The data type of of collections and tuples
 */
public class CollectionType {
    
    private final DataType dataType;
    private final Validation validation;

    CollectionType(DataType dataType, Validation validation) {
        this.dataType = dataType;
        this.validation = validation;
    }

    /**
     * Returns the data type of the collection type
     * @return the data type
     */
    public final DataType getDataType() {
        return dataType;
    }

    /**
     * Returns the validation constraint of the collection type
     * @return the validation constraint
     */
    public final Validation getValidation() {
        return validation;
    }
    
    
}
