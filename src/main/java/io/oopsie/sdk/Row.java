package io.oopsie.sdk;

import io.oopsie.sdk.error.DataTypeException;
import java.util.Date;
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
     * Returns the value for name as Boolean.
     * 
     * @param name the name of the row column
     * @throws DataTypeException
     * @return valus as Boolean
     * @throws IllegalArgumentException
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public Long getLong(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.NUMBER_BIG_INTEGER);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        // response from json produces Integers when numbers are low
        // calling this method expects Long value ..
        long primitive;
        if(val instanceof Integer) {
            primitive = ((Integer)val).longValue();
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public Double getDouble(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.NUMBER_DECIMAL);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        // response from json produces Integers or Longs whwn no decimals are present ... 
        // calling this method expects Double value ..
        double primitive;
        if(val instanceof Integer) {
            primitive = (double)((Integer)val).intValue();
        } else if(val instanceof Long) {
            primitive = (double)((Long)val).longValue();
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public Date getTimestamp(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.TIMESTAMP);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return new Date((Long)val);
    }
    
    /**
     * Returns the value for name as Set.
     * 
     * @param name the name of the row column
     * @return valus as Set
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * Returns the value for name as UUID.
     * 
     * @param name the name of the row column
     * @return value as UUID
     * @throws DataTypeException
     * @throws IllegalArgumentException
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
     * Returns the primary key of the relation resource rentity. A map with
     * PK attribute names mapped to their values.
     * @return a relation map
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public Map<String, Object> getRelation(String name) throws DataTypeException, IllegalArgumentException {
        checkNameAndType(name, DataType.RELATION);
        Object val = row.get(name);
        if(val == null) {
            return null;
        }
        return (Map)val;
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
        return (columnMetas.keySet().contains(name) || columnMetas.keySet().contains(name.substring(0, name.lastIndexOf("_data"))));
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
     * Prints out the row columns and their values.
     * @return string
     */
    @Override
    public String toString() {
        String string = "";
        for(String col : row.keySet()) {
            String val = row.get(col) != null ? row.get(col).toString() : "null";
            string = String.join("", string, col, "=", val , "\t");
        }
        return string;
    }
}
