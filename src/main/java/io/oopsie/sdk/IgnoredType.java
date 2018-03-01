package io.oopsie.sdk;

import com.google.common.collect.Sets;
import java.util.Set;

public enum IgnoredType {
    CID,
    CRB;
    
    public static Set<String> names() {
        return Sets.newHashSet(CID.name(), CRB.name());
    }
}
