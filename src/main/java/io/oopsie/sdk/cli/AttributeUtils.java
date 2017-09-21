package io.oopsie.sdk.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oopsie.sdk.DataType;
import io.oopsie.sdk.Resource;
import io.oopsie.sdk.SettableAttribute;
import io.oopsie.sdk.error.ModelException;
import java.util.Map;

class AttributeUtils {
    
    private static final ObjectMapper mapper = new ObjectMapper();

    static Object getValueObject(Resource resource, String attributeName, String value) throws ModelException {
        
           if(!resource.getAllSettableAttributeNames().contains(attributeName)) {
               throw new ModelException("Attribute name '" + attributeName + "' is not part of this model.");
           }
           
           SettableAttribute settable = resource.getRegularAttributes().get(attributeName);
           if(settable == null) {
               settable = resource.getClusterKeys().get(attributeName);
           }
           if(settable == null) {
               settable = resource.getPartitionKeys().get(attributeName);
           }
           DataType type = settable.getType();
           return createValue(attributeName,  value, type);
    }
    
    private static Object createValue(String attributeName, String value, DataType type) {
        Object object = null;
        try {
            switch(type) {
                case TEXT_4:
                case TEXT_8:
                case TEXT_16:
                case TEXT_32:
                case TEXT_128:
                case TEXT_1024:
                case TEXT_8192:
                case TEXT_65536:
                case TEXT_524288:
                    object =  value;
                    break;
                case BOOLEAN:
                    if(!value.equalsIgnoreCase(Boolean.TRUE.toString()) || !value.equalsIgnoreCase(Boolean.FALSE.toString())){
                        // lazily throwing to be catched ...
                        throw new RuntimeException();
                    }
                    object =  Boolean.valueOf(value);
                    break;
                case NUMBER_INTEGER:
                    object = Integer.valueOf(value);
                    break;
                case RELATION:
                    object = mapper.readValue(value, Map.class);
                    break;
                default:
                    // lazily throwing to be catched ...
                    throw new RuntimeException();
            }
        } catch(Exception e) {
            throw new IllegalArgumentException("'" + value + "' can't be cast to type '" + type + "'.");
        }
        return object;
    }
}
