package io.oopsie.sdk.model;

import java.util.Map;

class Applications {
    
    private final Map<String, Application> applications;

    Applications(Map<String, Application> applications) {
        this.applications = applications;
    }
    
    final Application getApplication(String name) {
        return this.applications.get(name);
    }
}
