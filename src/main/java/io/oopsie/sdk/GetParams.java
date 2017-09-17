package io.oopsie.sdk;

public enum GetParams {
    
    ID,
    PK,
    LIMIT("_limit"),
    EXPAND("_expandRelations");
    
    private String param;

    private GetParams() {
    }

    private GetParams(String param) {
        this.param = param;
    }
}
