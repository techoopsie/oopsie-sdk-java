package io.oopsie.sdk.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Resource {

    private final UUID resourceId;
    private final String name;
    private final Map<String, Attribute> attributes;
    private final Map<String, PartitionKey> partitionKeys;
    private final Map<String, ClusterKey> clusterKeys;
    private final Map<String, View> views;
    private final Map<String, Auth> auths;

    Resource(
            UUID resourceId,
            String name,
            Map<String, Attribute> attributes,
            Map<String, PartitionKey> partitionKeys,
            Map<String, ClusterKey> clusterKeys,
            Map<String, View> views,
            Map<String, Auth> auths) {

        this.resourceId = resourceId;
        this.name = name;
        this.attributes = attributes;
        this.partitionKeys = partitionKeys;
        this.clusterKeys = clusterKeys;
        this.views = views;
        this.auths = auths;
    }

    final UUID getResourceId() {
        return resourceId;
    }

    public final String getName() {
        return name;
    }
    
    /**
     * Returns all attributes names including partition
     * and clustering attributes. This method includes oopsie system
     * attribute names.
     *
     * @return all attribute names
     */
    public final Set<String> getAllAttributeNames() {
        Set<String> all = new HashSet();
        all.addAll(getAttributeNames());
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        return Collections.unmodifiableSet(all);
    }
    
    /**
     * Returns all attribute names that are 
     * either partition or clustering attribute.This method includes oopsie system
     * attribute names.
     * 
     * @return all primary key attributes
     */
    public final Set<String> getPrimaryKeyNames() {
        Set<String> all = new HashSet();
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        return Collections.unmodifiableSet(all);
    }
    
    /**
     * Returns all settable attribute names that are 
     * either partition or clustering attribute.This method excludes oopsie system
     * attribute names.
     * 
     * @return all settable primary key attributes
     */
    public final Set<String> getSettablePrimaryKeyNames() {
        Set<String> all = new HashSet();
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        all.remove("eid");
        all.remove("cid");
        return Collections.unmodifiableSet(all);
    }
    
    /**
     * Returns all settable attributes names including partition
     * and clustering attributes. This method excludes oopsie system
     * attribute names.
     *
     * @return all settable attribute names
     */
    public final Set<String> getAllSettableAttributeNames() {
        Set<String> all = new HashSet();
        all.addAll(getAttributeNames());
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        all.remove("cid");
        all.remove("eid");
        all.remove("cra");
        all.remove("crb");
        all.remove("cha");
        all.remove("chb");
        return Collections.unmodifiableSet(all);
    }

    /**
     * Returns all regular attributes.
     *
     * @return all regular attribute names
     */
    public final Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }

    /**
     * Returns all available partition keys including
     * non settable oopsie system partition keys.
     *
     * @return  all partition key names
     */
    public final Set<String> getPartitionKeyNames() {
        return Collections.unmodifiableSet(partitionKeys.keySet());
    }

    /**
     * Returns  all available cluster keys including
     * non settable oopsie system clustering keys.
     *
     * @return  all cluster key names
     */
    public final Set<String> getClusterKeyNames() {
        return Collections.unmodifiableSet(clusterKeys.keySet());
    }

    /**
     * Returns all available views.
     *
     * @return all of view names
     */
    public final Set<String> getViewNames() {
        return Collections.unmodifiableSet(views.keySet());
    }

    /**
     * Returns all auth names for this {@link Resource}.
     *
     * @return auths
     */
    public final Set<String> getAuthNames() {
        return Collections.unmodifiableSet(auths.keySet());
    }

    /**
     * Returns a {@link CreateStatement}.
     * 
     * @see CreateStatement#withParam(java.lang.String, java.lang.Object) 
     * @see CreateStatement#withParams(java.util.Map) 
     * @return a {@link CreateStatement}
     */
    public CreateStatement create() {
        return new CreateStatement(this);
    }
    
    /**
     * Returns a {@link GetStatement} for the passed in {@link Resource}
     * entity id.
     * 
     * @param entityId
     * @return a {@link GetStatement}
     */
    public GetStatement get(UUID entityId) {
        return new GetStatement(this, entityId);
    }
    
    /**
     * Returns a {@link GetStatement} for the passed in {@link Resource}
     * entity id.
     * 
     * @param entityId
     * @param queryParams query parameters
     * @return a {@link GetStatement}
     */
    public GetStatement get(UUID entityId, Map<String, Object> queryParams) {
        return new GetStatement(this, queryParams, entityId);
    }
    
    /**
     * Returns a {@link GetStatement} to retrive all entities. Note that the
     * statment is restricted with a default fetch size limit.
     * 
     * @return a {@link GetStatement}
     */
    public GetStatement get() {
        return new GetStatement(this);
    }
    
    /**
     * Returns a {@link GetStatement} to retrive all entities. Note that the
     * statment is restricted with a default fetch size limit.
     * 
     * @param queryParams query parameters
     * @return a {@link GetStatement}
     */
    public GetStatement get(Map<String, Object> queryParams) {
        return new GetStatement(this, queryParams);
    }
}
