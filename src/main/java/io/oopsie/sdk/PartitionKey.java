package io.oopsie.sdk;

import java.util.UUID;

public class PartitionKey extends Attribute {
    
    PartitionKey(UUID id, String name, DataType type, Validation validation) {
        super(id, name, type, null, validation);
    }
}
