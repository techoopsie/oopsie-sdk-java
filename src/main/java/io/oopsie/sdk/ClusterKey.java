package io.oopsie.sdk;

import java.util.UUID;

/**
 * A cluster key is part of a {@link View} to order data ASC or DESC.
 */
public class ClusterKey extends Attribute {
    
    //private final OrderBy orderBy;

//    ClusterKey(UUID id, String name, DataType type, OrderBy orderBy, Validation validation) {
//        super(id, name, type, validation);
//        this.orderBy = orderBy;
//    }
    
    ClusterKey(UUID id, String name, DataType type, Validation validation) {
        super(id, name, type, null, validation);
    }

//    /**
//     * Returns the order by of the cluster key
//     * @return an order by
//     */
//    public final OrderBy getOrderBy() {
//        return orderBy;
//    }
}
