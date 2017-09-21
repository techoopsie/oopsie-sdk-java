package io.oopsie.sdk;

public enum CLI_ReadParams {
    
    RESOURCE("--resource="),
    ID("--id="),
    PK("--pk="),
    LIMIT("--limit=", "_limit"),
    EXPAND("--expand=", "_expandRelations");
    
    private String command;
    private String queryParam;

    private CLI_ReadParams(String command) {
        this.command = command;
    }

    private CLI_ReadParams(String command, String param) {
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
