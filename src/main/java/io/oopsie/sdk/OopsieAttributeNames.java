package io.oopsie.sdk;

/**
 * oopsie system column names.
 */
public enum OopsieAttributeNames {
    
    cid,
    eid,
    crb,
    cra,
    chb,
    cha;
    
    public static boolean containsName(String name) {
        
        boolean contains = true;
        try {
            OopsieAttributeNames.valueOf(name);
        } catch(IllegalArgumentException e) {
            contains = false;
        }
        return contains;
    }
}
