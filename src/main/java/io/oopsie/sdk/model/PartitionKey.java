package io.oopsie.sdk.model;

import java.util.UUID;

class PartitionKey {
    
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

    final UUID getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final UUID getRelationId() {
        return relationId;
    }

    final DataType getType() {
        return type;
    }
}
