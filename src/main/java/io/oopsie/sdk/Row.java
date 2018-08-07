package io.oopsie.sdk;

import io.oopsie.sdk.error.DataTypeException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Holds information for a {@link Resource} entity.
 */
public class Row {
    
    private final Map<String, ResultSet.RowColumnMetaData> columnMetas;
    private final Map<String, Object> row;
    
    /**
     * Internally used by SDK to create rows of fetched data.
     * @param data the row data
     */
    Row(Map<String, ResultSet.RowColumnMetaData> columnMetas, Map<String, Object> data) {
        this.columnMetas = columnMetas;
        this.row = data;
    }
    
    /**
     * Returns the value object related to passed in column name. 
     * @param name name of the column to fetch
     * @return the value of the named column.
     */
    public Object get(String name) {
        
        Object value = null;
        DataType dataType = columnMetas.get(name).getDataType();
        switch(dataType) {
            case BOOLEAN:
                value = getBool(name);
                break;
            case CHANGED_AT:
                value = getChangedAt(name);
                break;
            case CREATED_AT:
                value = getCreatedAt(name);
                break;
            case TIMESTAMP:
                value = getTimestamp(name);
                break;
            case UUID:
                value = getUUID(name);
                break;
            case CREATED_BY:
                value = getCreatedBy(name);
                break;
            case CHANGED_BY:
                value = getChangedBy(name);
                break;
            case COLLECTION_LIST:
                value = getList(name);
                break;
            case COLLECTION_MAP:
                value = getMap(name);
                break;
            case COLLECTION_SET:
                value = getSet(name);
                break;
            case NUMBER_BIG_INTEGER:
                value = getLong(name);
                break;
            case NUMBER_DECIMAL:
                value = getDouble(name);
                break;
            case NUMBER_INTEGER:
                value = getInt(name);
                break;
            case TEXT:
                value = getString(name);
                break;
            case TUPLE:
                value = getTuple(name);
                break;
            default:
                throw new DataTypeException("Data tyep not in use: " + dataType.name());
        }
        return value;
    }
    
    /**
     * Returns the value for name as Boolean.
     * 
     * @param name the name of the row column
     * @throws DataTypeException if not able to cast to Boolean
     * @return valus as Boolean
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Boolean getBool(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.BOOLEAN);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (Boolean)row.get(name);
    }
    
    /**
     * Returns the value for name as Integer.
     * 
     * @param name the name of the row column
     * @return valus as Integer
     * @throws DataTypeException if not able to cast to Integer
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Integer getInt(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.NUMBER_INTEGER);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (Integer)row.get(name);
    }
    
    /**
     * Returns the value for name as Long.
     * 
     * @param name the name of the row column
     * @return valus as Long
     * @throws DataTypeException if not able to cast to Long
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Long getLong(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.NUMBER_BIG_INTEGER);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        // response from json produces Integers and Strings when numbers are low
        // calling this method expects Long value ..
        long primitive;
        if(val instanceof Integer) {
            primitive = ((Integer)val).longValue();
        } else if(val instanceof String) {
            primitive = Long.valueOf((String)val);
        } else {
            primitive =(Long)val;
        }
        return primitive;
    }
    
    /**
     * Returns the value for name as Double.
     * 
     * @param name the name of the row column
     * @return valus as double
     * @throws DataTypeException if not able to cast to Double
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Double getDouble(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.NUMBER_DECIMAL);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        // response from json is inconsistent, produces Integers, Longs and String
        //when no decimals are present ... 
        // calling this method expects Double value ..
        double primitive;
        if(val instanceof Integer) {
            primitive = (double)((Integer)val);
        } else if(val instanceof Long) {
            primitive = (double)((Long)val);
        } else if(val instanceof String) {
            primitive = Double.valueOf(val.toString());
        } else {
            primitive =(Double)val;
        }
        return primitive;
    }
    
    /**
     * Returns the value for name as String.
     * 
     * @param name the name of the row column
     * @return valus as String
     * @throws DataTypeException if not able to cast to String
     * @throws IllegalArgumentException if name is not part of this row
     */
    public String getString(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.TEXT);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return val.toString();
    }
    
    /**
     * Returns the value for name as Date.
     * 
     * @param name the name of the row column
     * @return valus as Date
     * @throws DataTypeException if not able to cast to Date
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Date getTimestamp(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.TIMESTAMP);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        Instant instant = Instant.parse(val.toString());
        Date parsedTimestamp = Date.from(instant);
        return parsedTimestamp;
    }
    
    /**
     * Returns the value for name as Date.
     * 
     * @param name the name of the row column
     * @return valus as Date
     * @throws DataTypeException if not able to cast to Date
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Date getChangedAt(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.CHANGED_AT);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        Instant instant = Instant.parse(val.toString());
        Date parsedTimestamp = Date.from(instant);
        return parsedTimestamp;
    }
    
    /**
     * Returns the value for name as Date.
     * 
     * @param name the name of the row column
     * @return valus as Date
     * @throws DataTypeException if not able to cast to Date
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Date getCreatedAt(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.CREATED_AT);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        Instant instant = Instant.parse(val.toString());
        Date parsedTimestamp = Date.from(instant);
        return parsedTimestamp;
    }
    
    /**
     * Returns the value for name as Set.
     * 
     * @param name the name of the row column
     * @return valus as Set
     * @throws DataTypeException if not able to cast to Set
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Set getSet(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.COLLECTION_SET);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return new HashSet((List)val);
    }
    
    /**
     * Returns the value for name as List.
     * 
     * @param name the name of the row column
     * @return valus as List
     * @throws DataTypeException if not able to cast to List
     * @throws IllegalArgumentException if name is not part of this row
     */
    public List getList(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.COLLECTION_LIST);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (List)val;
    }
    
    /**
     * Returns the value for name as Map.
     * 
     * @param name the name of the row column
     * @return valus as Map
     * @throws DataTypeException if not able to cast to Map
     * @throws IllegalArgumentException if name is not part of this row
     */
    public Map getMap(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.COLLECTION_MAP);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (Map)val;
    }
    
    /**
     * Returns the tuple value for name as List.
     * 
     * @param name the name of the row column
     * @return valus as List
     * @throws DataTypeException if not able to cast to List
     * @throws IllegalArgumentException if name is not part of this row
     */
    public List getTuple(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.TUPLE);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (List)val;
    }
    
    /**
     * Returns the value for name as UUID.
     * 
     * @param name the name of the row column
     * @return value as UUID
     * @throws DataTypeException if not able to cast to UUID
     * @throws IllegalArgumentException if name is not part of this row
     */
    public UUID getUUID(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.UUID);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return UUID.fromString(val.toString());
    }
    
    /**
     * Returns the value for name as UUID.
     * 
     * @param name the name of the row column
     * @return value as UUID
     * @throws DataTypeException if not able to cast to UUID
     * @throws IllegalArgumentException if name is not part of this row
     */
    public UUID getChangedBy(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.CHANGED_BY);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return UUID.fromString(val.toString());
    }
    
    /**
     * Returns the value for name as UUID.
     * 
     * @param name the name of the row column
     * @return value as UUID
     * @throws DataTypeException if not able to cast to UUID
     * @throws IllegalArgumentException if name is not part of this row
     */
    public UUID getCreatedBy(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.CREATED_BY);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return UUID.fromString(val.toString());
    }
    
    /**
     * Returns the meta data for the specified row column.
     * @param name the name of the column
     * @return row column meta data
     */
    public ResultSet.RowColumnMetaData getMeta(String name) {
        return columnMetas.get(name);
    }
    
    
    private boolean isNameValid(String name) throws IllegalArgumentException {
        return columnMetas.keySet().contains(name);
    }
    
    private void checkNameAndType(String name, DataType expected) {
        
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        
        DataType colType = getMeta(name).getDataType();
        if(!colType.equals(expected)) {
            throw new DataTypeException(
                    "Can't cast " + colType.getHumanName() + " to "
                            + expected.getHumanName() + ".");
        }
    }
    
    /**
     * Returns a map of all values mapped to their attribute namnes useful for
     * in conjuction with {@link SaveStatement#withParams(java.util.Map)} when only changing
     * a few of the {@link Resource} entity's values before saving the data. As
     * of now only HTTP PUT (updating all) values is supported. In the future
     * a we will support HHTP PATCH, i.e. updating an entity partitially without
     * having to sewt all values when calling save.
     * @return a map will all values mapped to their attribute names.
     */
    public Map<String, Object> getAsParams() {
        return new HashMap(row);
    }

    /**
     * Prints out the row columns and their values.
     * @return string
     */
    @Override
    public String toString() {
        String string = "";
        for(String col : columnMetas.keySet()) {
            String val = row.get(col) != null ? row.get(col).toString() : "null";
            string = String.join("", string, col, "=", val , "\n");
        }
        return string;
    }
}
