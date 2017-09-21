package io.oopsie.sdk;

public enum CLI_CreateParams {
    
    ATTRIB("@:");
    
    private String command;

    private CLI_CreateParams(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
