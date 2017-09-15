package io.oopsie.sdk.model;

public enum DataType {
    
    BOOLEAN("Boolean"),
    
    // CLOB("text", 1048576), // 524288 characters/part
    
    TEXT_4("Text 4"), //4 characters
    TEXT_8("Text 8"), //8 characters
    TEXT_16("Text 16"), //16 characters
    TEXT_32("Text 32"), //32 characters
    TEXT_128("Text 128"), //128 characters
    TEXT_1024("Text 1024"), //1024 characters
    TEXT_8192("Text 8K"), //8192 characters
    TEXT_65536("Text 65K"), //65536 characters
    TEXT_524288("Text 524K"), //524288 characters
    
    
//    BLOB("blob", 1048576), // 1048576 bytes/part
//    FILE("blob", 1048576), // 1048576 bytes/part
//    IMAGE("blob", 1048576), // 1048576 bytes/part
//    VIDEO("blob", 1048576), // 1048576 bytes/part
//    AUDIO("blob", 1048576), // 1048576 bytes/part
//    TINY_BLOB("blob", 1048576), // 1048576 bytes/part
//    IMAGE_THUMBNAIL("blob", 524288), // NOT SPLIT TO PARTS
//    VIDEO_CLIP("blob", 1048576), // NOT SPLIT TO PARTS
//    VIDEO_THUMBNAIL("blob", 524288), // NOT SPLIT TO PARTS
//    AUDIO_CLIP("blob", 524288),  // NOT SPLIT TO PARTS
    
    
    TIME("Time"), // Time string, such as 13:30:54.234
    DATE("Date"), // Date string, such as 2015-05-03
    TIMESTAMP("Timestamp"),    
    
    NUMBER_DECIMAL("Decimal"),
    NUMBER_BIG_INTEGER("Long"),
    NUMBER_INTEGER("Integer"),
    
    UUID("UUID"),
    UUID_TIME("TIMEUUID"),
    
    RELATION("Relation"),
    //RELATION_MANY_TO_MANY("Decimal", "string", "map", 0),
    
    
    COLLECTION_SET("Set"),
    COLLECTION_LIST("List"),
    COLLECTION_MAP("Map"),
    
    TUPLE("Tuple");
    
//    GEO_POINT("geo_point", 32*2),
//    GEO_PATH("geo_path", 0),
//    GEO_AREA("geo_area", 0);
    
    private final String humanName;
    
    private DataType(String humanName) {
        this.humanName = humanName;
    }
    
    public String getHumanName() {
        return humanName;
    }
    
}
