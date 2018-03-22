package io.oopsie.sdk;

import com.google.common.collect.Sets;
import java.util.Set;

public enum IgnoredType {
    
    // using lowercase so we don't have to do "toUpperCase" when comparing  ...
    cid,
    crb;
    
    public static Set<String> names() {
        return Sets.newHashSet(cid.name(), crb.name());
    }
}
