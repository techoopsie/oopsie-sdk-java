package io.oopsie.sdk;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * A {@link Resource} attribute. 
 */
public class Attribute {
    
    private final UUID id;
    private final String name;
    private final DataType type;
    private final List<CollectionType> collectionTypes;
    private final Validation validation;

    Attribute(UUID id, String name, DataType type,
            List<CollectionType> collectionType, Validation validation) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.collectionTypes = collectionType;
        this.validation = validation;
    }
    
    /**
     * Returns the id of the attribute.
     * @return attribute id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Returns the name of the attribute.
     * @return the attribute name
     */
    public final String getName() {
        return name;
    }

    /**
     * The data type of the attribute.
     * @return the data type
     */
    public final DataType getType() {
        return type;
    }

    public List<CollectionType> getCollectionTypes() {
        return collectionTypes;
    }

    /**
     * The validation constraints of the attributes data value.
     * @return the validation constraints
     */
    public final Validation getValidation() {
        return validation;
    }

    @Override
    public String toString() {
        return String.join("", name, ": " + type);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        return Objects.equals(this.id, other.id);
    }
}
