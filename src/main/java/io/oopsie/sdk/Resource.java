package io.oopsie.sdk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Resource {

    private final UUID resourceId;
    private final String name;
    private final Map<String, RegularAttribute> attributes;
    private final Map<String, PartitionKey> partitionKeys;
    private final Map<String, ClusterKey> clusterKeys;
    private final Map<String, View> views;
    private final Map<String, Auth> auths;

    Resource(
            UUID resourceId,
            String name,
            Map<String, RegularAttribute> attributes,
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

    
    /**
     * Returns the resource id.
     * @return a id
     */
    public final UUID getResourceId() {
        return resourceId;
    }

    /**
     * Returns the resource name.
     * @return a name
     */
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
     * attribute names, but includes "eid".
     * 
     * @return all settable primary key attributes
     */
    public final Set<String> getSettablePrimaryKeyNames() {
        Set<String> all = new HashSet();
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        all.remove("cid");
        return Collections.unmodifiableSet(all);
    }
    
    /**
     * Returns all settable attributes names including partition
     * and clustering attributes. This method excludes oopsie system
     * attribute names, but includes "eid".
     *
     * @return all settable attribute names
     */
    public final Set<String> getAllSettableAttributeNames() {
        Set<String> all = new HashSet();
        all.addAll(getAttributeNames());
        all.addAll(getPartitionKeyNames());
        all.addAll(getClusterKeyNames());
        all.remove("cid");
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
     * Returns all regular attributes.
     * @return regular attributes
     */
    public final Map<String, RegularAttribute> getRegularAttributes() {
        return Collections.unmodifiableMap(attributes);
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
     * Returns all Partition keys.
     * @return partition keys.
     */
    public final Map<String, PartitionKey> getPartitionKeys() {
        return Collections.unmodifiableMap(partitionKeys);
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
     * Returns the cluster keys.
     * @return cluster keys
     */
    public Map<String, ClusterKey> getClusterKeys() {
        return Collections.unmodifiableMap(clusterKeys);
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
     * Returns the attribute with passed name.
     * @param name the name of the attribute.
     * @return the attribute or null
     */
    public Attribute getAttribute(String name) {
        
        Attribute attrib = this.attributes.get(name);
        if(attrib != null) {
            return attrib;
        }
        
        attrib = clusterKeys.get(name);
        if(attrib != null) {
            return attrib;
        }
        
        return this.partitionKeys.get(name);
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
     * Returns a {@link SaveStatement} for this resource. You need to set
     * the specific reource enitity's primary key params.
     * 
     * @return a {@link  SaveStatement}
     * @see SaveStatement#withParam(java.lang.String, java.lang.Object)
     * @see SaveStatement#withParams(java.util.Map) 
     */
    public SaveStatement save() {
        return new SaveStatement(this);
    }
    
    /**
     * Returns a {@link GetStatement}. Executing the statement as is will
     * fetch all resource entitities, limited by fetch size. You need to set
     * the specific reource enitity's primary key params to fetch a specific
     * entity.
     * 
     * @return a {@link GetStatement}
     * @see GetStatement#withParam(java.lang.String, java.lang.Object)
     * @see GetStatement#withParams(java.util.Map) 
     */
    public GetStatement get() {
        return new GetStatement(this);
    }
    
    /**
     * Returns a {@link DeleteStatement} for this resource. You need to set
     * the specific reource enitity's primary key params.
     * 
     * @return a {@link  DeleteStatement}
     * @see DeleteStatement#withParam(java.lang.String, java.lang.Object)
     * @see DeleteStatement#withParams(java.util.Map) 
     */
    public DeleteStatement delete() {
        return new DeleteStatement(this);
    }
}
