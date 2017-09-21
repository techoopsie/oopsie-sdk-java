package io.oopsie.sdk;

import java.util.UUID;

public class ClusterKey implements SettableAttribute {
    
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

    /**
     * Returns the id of the cluster key
     * @return an id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Returns the name of the cluster key
     * @return a anme
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the relation id of the cluster key
     * @return a relation id
     */
    public final UUID getRelationId() {
        return relationId;
    }

    /**
     * Returns the type of the cluster key
     * @return a type
     */
    public DataType getType() {
        return type;
    }

    /**
     * Returns the order by of the cluster key
     * @return an order by
     */
    public OrderBy getOrderBy() {
        return orderBy;
    }
}
