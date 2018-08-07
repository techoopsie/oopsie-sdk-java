package io.oopsie.sdk;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.UUID;

public class View {
    
    private final UUID id;
    private final String name;
    private final boolean primary;
    private final LinkedHashMap<String, PartitionKey> partitionKeys;
    private final LinkedHashMap<String, ClusterKey> clusterKeys;

    View(UUID id, String name, boolean primary, LinkedHashMap<String, PartitionKey> partitionKeys,
            LinkedHashMap<String, ClusterKey> clusterKeys) {
        this.id = id;
        this.name = name;
        this.primary = primary;
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
     * Returns the partition keys of the view. The keys are ordered as defined
     * in the View model.
     * @return partition keys
     * @see #getClusterKeys()
     * @see #getPrimaryKey() 
     */
    public final LinkedHashMap<String, PartitionKey> getPartitionKeys() {
        return partitionKeys;
    }

    /**
     * Returns the cluster keys of the view. The keys are ordered as defined
     * in the View model.
     * @return the cluster keys
     * @see #getPartitionKeys() 
     * @see #getPrimaryKey() 
     */
    public final LinkedHashMap<String, ClusterKey> getClusterKeys() {
        return clusterKeys;
    }
    
    /**
     * Retuns the view's "primary key", i.e. an ordered map of this view's
     * partition keys (if any) and clustering keys (at least one).
     * @return the view's primary key
     * @see #getPartitionKeys() 
     * @see #getClusterKeys() 
     */
    public LinkedHashMap<String, Attribute> getPrimaryKey() {
        LinkedHashMap<String, Attribute> pk = new LinkedHashMap();
        pk.putAll(getPartitionKeys());
        pk.putAll(getClusterKeys());
        return pk;
    }

    /**
     * Return true if this {@link View} is the primary view of
     * of the {@link Resource}.
     * @return true if primary
     */
    public final boolean isPrimary() {
        return primary;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.id);
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
        final View other = (View) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
