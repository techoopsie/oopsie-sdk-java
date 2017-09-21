package io.oopsie.sdk;

import java.util.UUID;

public class Auth {
    
    private final UUID id;
    private final String name;
    private final Permission permission;

    Auth(UUID id, String name, Permission permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }

    /**
     * Returns the auth id
     * @return  an id
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Returns the name of the auth.
     * @return an auth name
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the auth permission.
     * @return a permission.
     */
    public final Permission getPermission() {
        return permission;
    }
}
