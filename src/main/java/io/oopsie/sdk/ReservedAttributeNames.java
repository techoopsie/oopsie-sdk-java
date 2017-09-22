package io.oopsie.sdk;

import java.util.EnumSet;

/**
 * oopsie system column names.
 */
public enum ReservedAttributeNames {
    
    cid,
    eid,
    crb,
    cra,
    chb,
    cha;
    
    public static boolean containsName(String name) {
        
        boolean contains = true;
        try {
            ReservedAttributeNames.valueOf(name);
        } catch(IllegalArgumentException e) {
            contains = false;
        }
        return contains;
    }
}
