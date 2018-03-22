package io.oopsie.sdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

class InitParser {
    
    static Applications parse(ResponseEntity entity) {
        return parseApps((Map)entity.getBody());
    }
    
    private static Applications parseApps(Map<String, Map> map) {
        
        Map<String, Application> parsedAppMap = new LinkedHashMap();
        
        map.keySet().forEach((String appName) -> {
            Map app = map.get(appName);
            parsedAppMap.put(appName, new Application(new Resources(parseResources((List)app.get("resources")))));
        });
        return new Applications(parsedAppMap);
    }
    
    
    private static Map<String, Resource> parseResources(List<Map> resList) {
        
        Map<String, Resource> parsedResources = new LinkedHashMap();
        for(Map resMap : resList) {

            UUID id = UUID.fromString((String)resMap.get("id"));
            String name = (String)resMap.get("name");
            
            Map<String, Attribute> attributes = parseAttributes(
                    (List)resMap.get("attributes")
            );
            Map<String, View> views = parseViews(
                    (List)resMap.get("views")
            );
            
            List authResp = (List)resMap.get("auths");
            Map<String, Auth> auths = authResp != null ? parseResourceAuths(authResp) : Collections.EMPTY_MAP;

            boolean authEnabled = (Boolean)resMap.get("authEnabled");
            Resource resource = new Resource(
                    id,
                    name,
                    attributes,
                    views,
                    auths,
                    authEnabled);
            parsedResources.put(name, resource);
        }
        return parsedResources;
    }
    
    private static Map<String, Attribute> parseAttributes(List<Map> attributes) {
        
        Map<String, Attribute> parsedAttributes = new LinkedHashMap();
        attributes.forEach((Map attribute) -> {
            
                String name = (String)attribute.get("name");
                if(!IgnoredType.names().contains(name)) {
                    String idVal = (String)attribute.get("id");
                    UUID id = UUID.fromString(idVal);
                    DataType type = DataType.valueOf((String)attribute.get("type"));
                    List<Map<String, Object>> collTypesList = (List)attribute.get("collectionTypes");
                    List<CollectionType> collectionTypes = new ArrayList();
                    if(collTypesList != null) {
                        collTypesList.forEach(cl -> {
                            DataType clType = DataType.valueOf(cl.get("datatype").toString());
                            Map<String, String> valMap = (Map)cl.get("validation");
                            Validation validation = null;
                            if(valMap != null) {
                                validation = new Validation(Long.valueOf(valMap.get("min")), Long.valueOf(valMap.get("max")));
                            }
                            collectionTypes.add(new CollectionType(clType, validation));
                        });
                    }

                    Map<String, String> valMap = (Map)attribute.get("validation");
                    Validation validation = null;
                    if(valMap != null) {
                        validation = new Validation(Long.valueOf(valMap.get("min")), Long.valueOf(valMap.get("max")));
                    }
                    parsedAttributes.put(name, new Attribute(id, name, type, collectionTypes, validation));
                }
        });
        return parsedAttributes;
    }
    
    private static LinkedHashMap<String, PartitionKey> parsePartitionKeys(List<Map> partitionKeys) {
        
        LinkedHashMap<String, PartitionKey> parsedPartitionKeys = new LinkedHashMap();
        partitionKeys.forEach((Map partitionKey) -> {
            
                String idVal = (String)partitionKey.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)partitionKey.get("name");
                
                Map<String, Long> valMap = (Map)partitionKey.get("validation");
                Validation validation = new Validation(valMap.get("min"), valMap.get("max"));
                
                DataType type = DataType.valueOf((String)partitionKey.get("type"));
                
                parsedPartitionKeys.put(
                        name,
                        new PartitionKey(id, name, type, validation)
                    );
        });
        return parsedPartitionKeys;
    }
    
    private static LinkedHashMap<String, ClusterKey> parseClusterKeys(List<Map> clusterKeys) {
        LinkedHashMap<String, ClusterKey> parsedClusterKey = new LinkedHashMap();
        clusterKeys.forEach((Map clusterKey) -> {
            
                String idVal = (String)clusterKey.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                
                String name = (String)clusterKey.get("name");
                
                Map<String, String> valMap = (Map)clusterKey.get("validation");
                Validation validation = null;
                if(valMap != null) {
                    validation = new Validation(Long.valueOf(valMap.get("min")), Long.valueOf(valMap.get("max")));
                }
                
                DataType type = DataType.valueOf((String)clusterKey.get("type"));
                
//                Object orderByVal = clusterKey.get("orderBy");
//                OrderBy orderBy = orderByVal != null ? OrderBy.valueOf((String)orderByVal) : null;
                
                parsedClusterKey.put(
                        name,
                        new ClusterKey(id, name, type, validation)
                    );
        });
        return parsedClusterKey;
    }
    
    private static Map<String, View> parseViews(List<Map> views) {
        Map<String, View> parsedViews = new LinkedHashMap();
        views.forEach((Map view) -> {
            
                String idVal = (String)view.get("id");
                UUID id = idVal != null ? UUID.fromString(idVal) : null;
                String name = (String)view.get("name");
                boolean primary = (Boolean)view.get("primary");
                
                LinkedHashMap<String, PartitionKey> partitionKeys = parsePartitionKeys(
                        (List)view.get("partitionKeys")
                );
                LinkedHashMap<String, ClusterKey> clusterKeys = parseClusterKeys(
                        (List)view.get("clusterKeys")
                );
                
                parsedViews.put(
                        name,
                        new View(id, name, primary, partitionKeys, clusterKeys)
                    );
        });
        return parsedViews;
    }
    
    private static Map<String, Auth> parseResourceAuths(List<Map> auths) {
        Map<String, Auth> parsedAuths = new LinkedHashMap();
        auths.forEach((Map auth) -> {
            
                UUID id = UUID.fromString((String)auth.get("id"));
                String name = (String)auth.get("name");
                Permission permission = Permission.valueOf((String)auth.get("permission"));
                parsedAuths.put(
                        name,
                        new Auth(id, name, permission)
                    );
        });
        return parsedAuths;
    }
    
}
