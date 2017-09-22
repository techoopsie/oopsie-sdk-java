package io.oopsie.sdk;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

class InitParser {
    
    static Applications parse(
            ResponseEntity entity) {
        return parseApps(
                (Map)entity.getBody());
    }
    
    private static Applications parseApps(
            Map<String, Map> map) {
        
        Map<String, Application> parsedAppMap = new LinkedHashMap();
        
        map.keySet().forEach((String appName) -> {
            Map app = map.get(appName);
            parsedAppMap.put(appName,
                    new Application(
                            new Resources(
                                    parseResources(
                                            (List)app.get("resources")
                                    )
                            )
                    ));
        });
        return new Applications(parsedAppMap);
    }
    
    
    private static Map<String, Resource> parseResources(List<Map> resList) {
        Map<String, Resource> parsedResources = new LinkedHashMap();
        for(Map resMap : resList) {

            UUID id = UUID.fromString((String)resMap.get("id"));
            String name = (String)resMap.get("name");
            
            Map<String, RegularAttribute> attributes = parseResourceAttributes(
                    (List)resMap.get("attributes")
            );
            Map<String, PartitionKey> partitionKeys = parseResourcePartitionKeys(
                    (List)resMap.get("partitionKeys")
            );
            Map<String, ClusterKey> clusterKeys = parseResourceClusterKeys(
                    (List)resMap.get("clusterKeys")
            );
            Map<String, View> views = parseResourceViews(
                    (List)resMap.get("views")
            );
            Map<String, Auth> auths = parseResourceAuths(
                    (List)resMap.get("auths")
            );

            Resource resource = new Resource(
                    id,
                    name,
                    attributes,
                    partitionKeys,
                    clusterKeys,
                    views,
                    auths);
            parsedResources.put(name, resource);
        }
        return parsedResources;
    }
    
    private static Map<String, RegularAttribute> parseResourceAttributes(List<Map> attributes) {
        
        Map<String, RegularAttribute> parsedAttributes = new LinkedHashMap();
        attributes.forEach((Map attribute) -> {
            
                String idVal = (String)attribute.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)attribute.get("name");
                
                Object relationVal = attribute.get("id");
                UUID relationId = relationVal != null ? UUID.fromString((String)relationVal) : null;
                
                DataType type = DataType.valueOf((String)attribute.get("type"));
                
                parsedAttributes.put(
                        name,
                        new RegularAttribute(id, name, relationId, type)
                    );
        });
        return parsedAttributes;
    }
    
    private static Map<String, PartitionKey> parseResourcePartitionKeys(List<Map> partitionKeys) {
        
        Map<String, PartitionKey> parsedPartitionKeys = new LinkedHashMap();
        partitionKeys.forEach((Map partitionKey) -> {
            
                String idVal = (String)partitionKey.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)partitionKey.get("name");
                
                Object relationVal = partitionKey.get("id");
                UUID relationId = relationVal != null ? UUID.fromString((String)relationVal) : null;
                
                DataType type = DataType.valueOf((String)partitionKey.get("type"));
                
                parsedPartitionKeys.put(
                        name,
                        new PartitionKey(id, name, relationId, type)
                    );
        });
        return parsedPartitionKeys;
    }
    
    private static Map<String, ClusterKey> parseResourceClusterKeys(List<Map> clusterKeys) {
        Map<String, ClusterKey> parsedClusterKey = new LinkedHashMap();
        clusterKeys.forEach((Map clusterKey) -> {
            
                String idVal = (String)clusterKey.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)clusterKey.get("name");
                
                Object relationVal = clusterKey.get("id");
                UUID relationId = relationVal != null ? UUID.fromString((String)relationVal) : null;
                
                DataType type = DataType.valueOf((String)clusterKey.get("type"));
                
                Object orderByVal = clusterKey.get("orderBy");
                OrderBy orderBy = orderByVal != null ? OrderBy.valueOf((String)orderByVal) : null;
                
                parsedClusterKey.put(
                        name,
                        new ClusterKey(id, name, relationId, type, orderBy)
                    );
        });
        return parsedClusterKey;
    }
    
    private static Map<String, View> parseResourceViews(List<Map> views) {
        Map<String, View> parsedViews = new LinkedHashMap();
        views.forEach((Map view) -> {
            
                String idVal = (String)view.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)view.get("name");
                
                Map<String, PartitionKey> partitionKeys = parseResourcePartitionKeys(
                        (List)view.get("partitionKeys")
                );
                Map<String, ClusterKey> clusterKeys = parseResourceClusterKeys(
                        (List)view.get("clusterKeys")
                );
                
                parsedViews.put(
                        name,
                        new View(id, name, partitionKeys, clusterKeys)
                    );
        });
        return parsedViews;
    }
    
    private static Map<String, Auth> parseResourceAuths(List<Map> auths) {
        Map<String, Auth> parsedAuths = new LinkedHashMap();
        auths.forEach((Map auth) -> {
            
                UUID id = UUID.fromString((String)auth.get("i"));
                String name = (String)auth.get("n");
                Permission permission = Permission.valueOf((String)auth.get("p"));
                parsedAuths.put(
                        name,
                        new Auth(id, name, permission)
                    );
        });
        return parsedAuths;
    }
    
}
