package io.oopsie.sdk;

import java.util.UUID;

public class PartitionKey implements Attribute {
    
    private final UUID id;
    private final String name;
    private final UUID relationId;
    private final DataType type;

    PartitionKey(UUID id, String name, UUID relationId , DataType type) {
        this.id = id;
        this.name = name;
        this.relationId = relationId;
        this.type = type;
    }

    /**
     * Returns the partition key's id.
     * @return an id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Returns the partition key's name.
     * @return a name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the partition key's relation id.
     * @return a relation id
     */
    public final UUID getRelationId() {
        return relationId;
    }

    /**
     * Returns the partition key's type.
     * @return a type
     */
    public DataType getType() {
        return type;
    }

    @Override
    public boolean isPrimaryKey() {
        return true;
    }

    @Override
    public boolean isPartitionKey() {
        return true;
    }

    @Override
    public boolean isClusterKey() {
        return false;
    }

    @Override
    public boolean isRegularColumn() {
        return false;
    }
    
    @Override
    public boolean isSystemColumn() {
        return OopsieAttributeNames.containsName(name);
    }
}
