package io.oopsie.sdk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Holds information ({@link Row}s) for an executed {@link Statement}.
 * 
 * Note, instances of this class are not thread safe.
 * 
 */
public class ResultSet implements Iterable<Row> {
    
    private final boolean applied;
    private final Statement statement;
    private final Deque<Row> rows = new ArrayDeque<>();
    
    /**
     * Used internally by SDK to produce a {@link Statement} result.
     * 
     * @param statement the statement that produced the result.
     * @param applied whether the {@link Statement} was appleid or not.
     */
    ResultSet(Statement statement, boolean applied) {
        this.statement = statement;
        this.applied = applied;   
    }
    
    /**
     * Used internally by SDK to produce a {@link Statement} result.
     * 
     * @param statement the statement that produced the result.
     * @param applied whether the {@link Statement} was appleid or not.
     * @param data
     */
    ResultSet(Statement statement, boolean applied, List<Map<String, Object>> data) {
        this.statement = statement;
        this.applied = applied;
        createRows(data);
    }
    
    /**
     * Returns true if the {@link ResultSet}s underlying {@link Statement}
     * successfully could fullfill its execution.
     * 
     * @return true if applied
     */
    public boolean wasApplied() {
        return applied;
    }
    
    /**
     * Returns true if this result has no more rows to fetch.
     * @return true if exhausted.
     */
    public boolean isExhausted() {
        return this.rows.isEmpty();
    }

    /**
     * Return the {@link Statement} that produced this {@link ResultSet}.
     * @return a {@link Statement}
     */
    public Statement getStatement() {
        return statement;
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<Row>() {
            @Override
            public boolean hasNext() {
                return !ResultSet.this.rows.isEmpty();
            }

            @Override
            public Row next() {
                return ResultSet.this.rows.poll();
            }
        };
    }
    
    /**
     * Returns all remaining rows in this result.
     * @return all remaining rows
     */
    public List<Row> all() {
        
        if (isExhausted()) {
            return Collections.emptyList();
        }
        
        List<Row> result = new ArrayList(rows.size());
        for (Row row : rows) {
            result.add(row);
        }
        return result;
    }
    
    /**
     * Returns the next row from this result. Null if exhausted.
     * @return bext row or null if exhausted.
     */
    public Row one() {
        return rows.poll();
    }

    private void createRows(List<Map<String, Object>> data) {
        data.forEach(dataRow -> {
                Row row = new Row(dataRow);
                rows.add(row);
        });
    }
    
}
