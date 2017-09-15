package io.oopsie.sdk.model;

import java.util.Map;
import java.util.UUID;

class View {
    
    private final UUID id;
    private final String name;
    private final Map<String, PartitionKey> partitionKeys;
    private final Map<String, ClusterKey> clusterKeys;

    View(UUID id, String name, Map<String, PartitionKey> partitionKeys,
            Map<String, ClusterKey> clusterKeys) {
        this.id = id;
        this.name = name;
        this.partitionKeys = partitionKeys;
        this.clusterKeys = clusterKeys;
    }

    final UUID getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final Map<String, PartitionKey> getPartitionKeys() {
        return partitionKeys;
    }

    final Map<String, ClusterKey> getClusterKeys() {
        return clusterKeys;
    }

}
