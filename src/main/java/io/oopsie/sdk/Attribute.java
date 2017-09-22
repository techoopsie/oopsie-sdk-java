package io.oopsie.sdk;

public interface Attribute {
    
    String getName();
    
    DataType getType();
    
    boolean isPrimaryKey();
    
    boolean isPartitionKey();
    
    boolean isClusterKey();
    
    boolean isRegularColumn();
    
    boolean isSystemColumn();
}
