package io.oopsie.sdk;

import java.util.Map;
import java.util.UUID;

public class View {
    
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

    /**
     * Returns the id of the view.
     * @return the id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Returns the name of the view.
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the partition keys of the view.
     * @return partition keys
     */
    public final Map<String, PartitionKey> getPartitionKeys() {
        return partitionKeys;
    }

    /**
     * Returns the cluster keys of the view.
     * @return the cluster keys
     */
    public final Map<String, ClusterKey> getClusterKeys() {
        return clusterKeys;
    }

}
