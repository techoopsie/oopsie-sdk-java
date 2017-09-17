package io.oopsie.sdk;

import java.util.UUID;


class Auth {
    
    private final UUID id;
    private final String name;
    private final Permission permission;

    Auth(UUID id, String name, Permission permission) {
        this.id = id;
        this.name = name;
        this.permission = permission;
    }

    final UUID getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final Permission getPermission() {
        return permission;
    }
}
