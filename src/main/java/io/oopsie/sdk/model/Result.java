package io.oopsie.sdk.model;

public class Result {
    
    private final boolean applied;
    private final Statement statement;
    
    /**
     * Used internally by SDK to produce a {@link Statement} result.
     * 
     * @param statement the statement that produced the result.
     * @param applied whether the {@link Statement} was appleid or not.
     */
    Result(Statement statement, boolean applied) {
        this.statement = statement;
        this.applied = applied;   
    }
    
    /**
     * Returns true if the {@link Result}s underlying {@link Statement}
     * successfully could fullfill its execution.
     * 
     * @return true if applied
     */
    public boolean wasApplied() {
        return applied;
    }
}
