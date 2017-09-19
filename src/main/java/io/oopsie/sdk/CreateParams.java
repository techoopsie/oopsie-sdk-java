package io.oopsie.sdk;

public enum CreateParams {
    
    ATTRIB("@:");
    
    private String command;

    private CreateParams(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}
