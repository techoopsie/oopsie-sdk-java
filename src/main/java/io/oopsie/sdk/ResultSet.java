package io.oopsie.sdk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Holds ({@link Row}s) information for an executed {@link Statement}.
 * 
 * Note, instances of this class are not thread safe.
 * 
 */
public class ResultSet implements Iterable<Row> {
    
    private final boolean applied;
    private final Statement statement;
    private final Deque<Row> rows = new ArrayDeque();
    private Map<String, RowColumnMetaData> columnMetaData;
    
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
     * @return next row or null if exhausted.
     */
    public Row one() {
        return rows.poll();
    }

    /**
     * Returns the names of all columns in this ResultSet. Note that some of
     * the columns are created internally as helper columns and not part of
     * the {@link Resource} model created by the user and therefore not
     * settable.
     * @return set of attribute names.
     */
    public Set<String> getColumnNames() {
        return columnMetaData != null ? columnMetaData.keySet() : Collections.EMPTY_SET;
    }

    /**
     * Returns the column meta data for the included row columns. Column name
     * is mapped to its RowColumnMetaData.
     * 
     * @return row column meta data.
     */
    public Map<String, RowColumnMetaData> getColumnMetaData() {
        return columnMetaData != null ? Collections.unmodifiableMap(columnMetaData) : Collections.EMPTY_MAP;
    }
    
    /**
     * Returns the column size.
     * @return column size
     */
    public int getColumnSize() {
        return getColumnMetaData().size();
    }

    private void createRows(List<Map<String, Object>> data) {
        
        // fetch first to crate result meta data
        if(!data.isEmpty() && data.get(0) != null) {
            Map tempMetaData = new TreeMap();
            data.get(0).keySet().forEach(colName -> {
                
                if(!IgnoredType.names().contains(colName)) {
                    Attribute attrib = statement.getResource().getAttribute(colName);
                    if(attrib  != null) {
                        tempMetaData.put(colName, new RowColumnMetaData(colName, attrib.getType()));
                    }
                }
            });
            columnMetaData = Collections.unmodifiableMap(tempMetaData);

            // create the actual rows without ignored types ...
            data.forEach(r -> {
                
                Map<String, Object> dataRow = new HashMap();
                columnMetaData.keySet().forEach(colName ->{
                    dataRow.put(colName, r.get(colName));
                });
                Row row = new Row(columnMetaData, dataRow);
                rows.add(row);
            });
        }
    }
    
    public static class RowColumnMetaData {

        private final String columnName;
        private final DataType dataType;
        
        private RowColumnMetaData(String columnName, DataType dataType) {
            
            this.columnName = columnName;
            this.dataType = dataType;
        }

        /**
         * The attribute name of the row column as declared on the resource.
         * @return column name
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * The data type of the column.
         * @return data type
         */
        public DataType getDataType() {
            return dataType;
        }

    }
    
}
