package io.oopsie.sdk;

public enum ReadParams {
    
    RESOURCE("--resource="),
    ID("--id="),
    PK("--pk="),
    LIMIT("--limit=", "_limit"),
    EXPAND("--expand=", "_expandRelations");
    
    private String command;
    private String queryParam;

    private ReadParams(String command) {
        this.command = command;
    }

    private ReadParams(String command, String param) {
        this(command);
        this.queryParam = param;
    }

    public String commandEquals() {
        return command;
    }
    
    public String command() {
        return command.substring(0, command.lastIndexOf("="));
    }

    public String queryParam() {
        return queryParam;
    }
}
