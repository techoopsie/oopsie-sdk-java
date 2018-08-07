package io.oopsie.sdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A handle to a specific resource defined in an OOPSIE Cloud {@link Site}.
 */
public class Resource {

    private final UUID resourceId;
    private final String name;
    private final Map<String, Attribute> attributes;
    private final Map<String, View> views;
    private final Map<String, Auth> auths;
    private final boolean authEnabled;

    /**
     * 
     * @param resourceId the resourceId
     * @param name the name
     * @param attributes the attributes
     * @param partitionKeys the partition keys
     * @param clusterKeys the cluster keys
     * @param views the views
     * @param auths the auths
     */
    Resource(
            UUID resourceId,
            String name,
            Map<String, Attribute> attributes,
            Map<String, View> views,
            Map<String, Auth> auths,
            boolean authEnabled) {

        this.resourceId = resourceId;
        this.name = name;
        this.attributes = attributes;
        this.views = views;
        this.auths = auths;
        this.authEnabled = authEnabled;
    }

    
    /**
     * Returns the resource id.
     * @return an id
     */
    public final UUID getResourceId() {
        return resourceId;
    }

    /**
     * Returnsd true if auth is enabled on this Resource
     * @return true if auth enabled
     */
    public boolean isAuthEnabled() {
        return authEnabled;
    }

    /**
     * Returns the resource name.
     * @return a name
     */
    public final String getName() {
        return name;
    }
    
    /**
     * Returns all attribute names.
     *
     * @return all attribute names
     */
    public final Set<String> getAttributeNames() {
        return Collections.unmodifiableSet(attributes.keySet());
    }
    
    /**
     * Returns all names of the attributes that can be set by client.
     * @return settable attributes.
     */
    public List<Attribute> getAllSettableAttributes() {
        
        List<Attribute> settableAttribs = new ArrayList();
        for(Attribute a : attributes.values()) {
            if(a.getName().equalsIgnoreCase("id")) {
                continue;
            } else if(
                a.getType().equals(DataType.CHANGED_AT)
                ||
                a.getType().equals(DataType.CHANGED_BY)
                ||
                a.getType().equals(DataType.CREATED_AT)
                ||
                a.getType().equals(DataType.CREATED_BY)) {
                continue;
            } else {
                settableAttribs.add(a);
            }
        }
        return Collections.unmodifiableList(settableAttribs);
    }
    
    /**
     * Returns all names of the attributes that can be set by client.
     * @return settable attribute names.
     */
    public Set<String> getAllSettableAttributeNames() {
        
        List<Attribute> settableAttribs = attributes.values().stream().filter(a -> 
                a.getName().equalsIgnoreCase("id")
                ||
                !a.getType().equals(DataType.CHANGED_AT)
                ||
                !a.getType().equals(DataType.CHANGED_BY)
                ||
                !a.getType().equals(DataType.CREATED_AT)
                ||
                !a.getType().equals(DataType.CREATED_BY)).collect(Collectors.toList());
        
        return settableAttribs.stream().map(a -> a.getName()).collect(Collectors.toSet());
    }
    
    /**
     * Retuns the "primary key" for the primary view, i.e. an ordered map of this view's
     * partition keys (if any) and clustering keys (at least one).
     * @return the primary view's primary key
     * @see #getPrimaryView()
     */
    public LinkedHashMap<String, Attribute> getPrimaryKey() {
        return getPrimaryView().getPrimaryKey();
    }
    
    /**
     * Returns the primary view.
     * @return the primary view
     */
    public View getPrimaryView() {
        return views.values().stream().filter(view -> view.isPrimary()).findAny().get();
    }
    
    /**
     * Returns the name of the primary view.
     * @return the primary view name
     */
    public final String getPrimaryViewName() {
        return views.values().stream().filter(view -> view.isPrimary()).findAny().get().getName();
    }

    /**
     * Returns a collection of all {@link View}s configured on this Resource.
     * @return a collection of Views.
     */
    public Collection<View> getViews() {
        return Collections.unmodifiableCollection(views.values());
    }
    
    /**
     * Returns the {@link View} related to passed in name.
     * @param name the name of the view
     * @return the named View
     */
    public View getView(String name) {
        return views.values().stream().filter(view -> view.getName().equals(name)).findAny().get();
    }
    
    /**
     * Returns all view names.
     * @return all of view names
     */
    public final Set<String> getViewNames() {
        return Collections.unmodifiableSet(views.keySet());
    }
    
    /**
     * Returns a collection of all {@link Auth}s configured on this Resource.
     * @return a collection of Auths.
     */
    public Collection<Auth> getAuths() {
        return Collections.unmodifiableCollection(auths.values());
    }
    
    /**
     * Returns the {@link Auth} related to passed in name.
     * @param name the name of the auth
     * @return the named Auth
     */
    public Auth getAuth(String name) {
        return auths.values().stream().filter(auth -> auth.getName().equalsIgnoreCase(name)).findAny().get();
    }
    
    /**
     * Returns all auth names.
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
        
        return this.attributes.get(name);
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
     * fetch all resource entitities, limited by fetch size, using the primary view. You need to set
     * the specific reource enitity's primary key params to fetch a specific
     * entity. If any preceeding PK param is missing the GET API will will resolve the correct
     * query by using passed in PK params. The GetStatement returned from this method will be same as calling
     * {@link #get(io.oopsie.sdk.View) } with the Resource's primary view.
     * 
     * @return a {@link GetStatement}
     * @see GetStatement#withParam(java.lang.String, java.lang.Object)
     * @see GetStatement#withParams(java.util.Map) 
     * @see #get(io.oopsie.sdk.View) 
     */
    public GetStatement get() {
        return new GetStatement(this);
    }
    
    /**
     * Returns a {@link GetStatement} for named {@link View}. Executing the statement as is will
     * fetch all resource entitities from the resource view, limited by fetch size. You need to set
     * the specific view enitity's primary key params to fetch a specific entity. If any
     * preceeding PK param is missing the GET API will will resolve the correct
     * query by using passed in PK params.
     * 
     * @param view the view
     * @return a {@link GetStatement}
     * @see GetStatement#withParam(java.lang.String, java.lang.Object)
     * @see GetStatement#withParams(java.util.Map)
     */
    public GetStatement get(View view) {
        return new GetStatement(this, view);
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

    /**
     * Returns all attributes for the Resource object.
     * @return all attribuets
     */
    public Collection<Attribute> getAttributes() {
        return Collections.unmodifiableCollection(attributes.values());
    }
    
    @Override
    public String toString() {
        return name;
    }
}
