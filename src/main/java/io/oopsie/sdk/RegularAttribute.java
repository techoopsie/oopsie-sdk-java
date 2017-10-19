package io.oopsie.sdk;

import java.util.UUID;

public class RegularAttribute implements Attribute {
    
    private final UUID id;
    private final String name;
    private final UUID relationId;
    private final DataType type;

    RegularAttribute(UUID id, String name, UUID relationId , DataType type) {
        this.id = id;
        this.name = name;
        this.relationId = relationId;
        this.type = type;
    }

    public final UUID getId() {
        return id;
    }

    public final String getName() {
        return name;
    }

    public final UUID getRelationId() {
        return relationId;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public boolean isPrimaryKey() {
        return false;
    }

    @Override
    public boolean isPartitionKey() {
        return false;
    }

    @Override
    public boolean isClusterKey() {
        return false;
    }

    @Override
    public boolean isRegularColumn() {
        return true;
    }
    
    @Override
    public boolean isSystemColumn() {
        return OopsieAttributeNames.containsName(name);
    }
}
