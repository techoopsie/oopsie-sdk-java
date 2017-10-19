package io.oopsie.sdk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * the {@link Resdource} model created by the user and therefore not
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

    private void createRows(List<Map<String, Object>> data) {
        
        // fetch first to crate result meta data
        if(!data.isEmpty() && data.get(0) != null) {
            
            Map tempMetaData = new LinkedHashMap();
            data.get(0).keySet().forEach(colName -> {

                Attribute attrib = statement.getResource().getAttribute(colName);
                DataType type;
                boolean systemColumn = true;
                boolean modelSupported = false;
                boolean expandColumn = false;
                if(attrib == null) {
                    // next is a very primitive way of deciding the expand column status ...
                    // important though is that it is contained within this block.
                    expandColumn = colName.toLowerCase().endsWith("_data");
                    if(expandColumn) {
                        type = DataType.EXPAND;
                    } else {
                        type = DataType.UNDEFINED;
                    }
                } else {
                    type = attrib.getType();
                    modelSupported = true;
                    systemColumn =  attrib.isSystemColumn();
                }
                tempMetaData.put(colName, new RowColumnMetaData(
                                        colName,
                                        type,
                                        systemColumn,
                                        modelSupported,
                                        expandColumn));
            });
            columnMetaData = Collections.unmodifiableMap(tempMetaData);

            // create the actual rows ...
            data.forEach(dataRow -> {
                    Row row = new Row(columnMetaData, dataRow);
                    rows.add(row);
            });
        }
    }
    
    public static class RowColumnMetaData {

        private String columnName;
        private DataType dataType;
        private boolean systemColumn;
        private boolean modelSupported;
        private boolean expandColumn;
        
        private RowColumnMetaData(String columnName, DataType dataType, boolean systemColumn,
                boolean modelSupported, boolean expandColumn) {
            
            if(!systemColumn && expandColumn) {
                throw new IllegalArgumentException("Can't be both expand column but not system column");
            }
            if(modelSupported && expandColumn) {
                throw new IllegalArgumentException("Can't be both model supported and expand column");
            }
            this.columnName = columnName;
            this.dataType = dataType;
            this.systemColumn = systemColumn;
            this.modelSupported = modelSupported;
            this.expandColumn = expandColumn;
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

        /**
         * Returns true if column and its data is generated by oopsie system.
         * @return true if system generated data.
         */
        public boolean isSystemColumn() {
            return systemColumn;
        }
        
        /**
         * Returns true if column is part of the {@link Resource} model supporting
         * the {@link ResultSet}. Also, the persisted data is inserted using creates
         * or updates from an oopsie client calling a {@link Site}'s api.
         * Note that even if data is part of model supported columns this method will
         * always return false if {@link #isExpandColumn()} returns true.
         * @return true if model supported.
         */
        public boolean isModelSupported() {
            return modelSupported;
        }

        /**
         * Returns true if column and its data is a result of a relation from one {@link Resource}
         * entity to another. Note that even if data is part of model supported columns the
         * {@link #isModelSupported()} will always return false if this method returns true.
         * @return true if expand column
         */
        public boolean isExpandColumn() {
            return expandColumn;
        }
    }
    
}
