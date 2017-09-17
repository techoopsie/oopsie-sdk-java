package io.oopsie.sdk;

import java.util.UUID;

class ClusterKey implements SettableAttribute {
    
    private final UUID id;
    private final String name;
    private final UUID relationId;
    private final DataType type;
    private final OrderBy orderBy;

    ClusterKey(UUID id, String name, UUID relationId , DataType type, OrderBy orderBy) {
        this.id = id;
        this.name = name;
        this.relationId = relationId;
        this.type = type;
        this.orderBy = orderBy;
    }

    final UUID getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final UUID getRelationId() {
        return relationId;
    }

    public DataType getType() {
        return type;
    }

    OrderBy getOrderBy() {
        return orderBy;
    }
}
