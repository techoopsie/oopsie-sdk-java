package io.oopsie.sdk;

public enum DataType {
    
    BOOLEAN("Boolean"),
    TEXT("Text"),
    TIMESTAMP("Timestamp"),
    NUMBER_DECIMAL("Decimal"),
    NUMBER_BIG_INTEGER("Long"),
    NUMBER_INTEGER("Integer"),
    UUID("UUID"),
    RELATION("Relation"),
    COLLECTION_SET("Set"),
    COLLECTION_LIST("List"),
    COLLECTION_MAP("Map"),
    TUPLE("Tuple"),
    EXPAND("Expand"),
    UNDEFINED("Undefined");
    
    private final String humanName;
    
    private DataType(String humanName) {
        this.humanName = humanName;
    }
    
    public String getHumanName() {
        return humanName;
    }
    
}
