package io.oopsie.sdk;

import io.oopsie.sdk.error.ModelException;

class AttributeUtils {

    static Object getValueObject(Resource resource, String attributeName, String value) throws ModelException {
        
           if(!resource.getAllSettableAttributeNames().contains(attributeName)) {
               throw new ModelException("Attribute name '" + attributeName + "' is not part of this model.");
           }
           
           SettableAttribute settable = resource.getAttributes().get(attributeName);
           if(settable == null) {
               settable = resource.getClusterKeys().get(attributeName);
           }
           if(settable == null) {
               settable = resource.getPartitionKeys().get(attributeName);
           }
           DataType type = settable.getType();
           return createValue(type, value);
    }
    
    private static Object createValue(DataType type, String value) {
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
