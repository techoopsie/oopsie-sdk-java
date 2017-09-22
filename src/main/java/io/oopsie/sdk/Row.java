package io.oopsie.sdk;

import io.oopsie.sdk.error.DataTypeException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Holds information for a {@link Resource} entity.
 */
public class Row {
    
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private Map<String, Object> row;
    
    /**
     * Internally used by SDK to create rows of fetched data.
     * @param data the row data
     */
    Row(Map<String, Object> data) {
        this.row = data;
    }
    
    
    /**
     * Returns the value for name as Object.
     * 
     * @param name the name of the row column
     * @return valeu as Object
     * @throws IllegalArgumentException
     */
    public Object get(String name) throws IllegalArgumentException {
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        return row.get(name);
    }
    
    /**
     * Returns the value for name as boolean.
     * 
     * @param name the name of the row column
     * @throws DataTypeException
     * @return valus as boolean
     * @throws IllegalArgumentException
     */
    public boolean getBool(String name) throws DataTypeException, IllegalArgumentException {
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            boolean bool = (Boolean)get(name);
            return bool;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to boolean.");
        }
    }
    
    /**
     * Returns the value for name as int.
     * 
     * @param name the name of the row column
     * @return valus as int
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public int getInt(String name) throws DataTypeException, IllegalArgumentException {
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            int i = (Integer)get(name);
            return i;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to int.");
        }
    }
    
    /**
     * Returns the value for name as long.
     * 
     * @param name the name of the row column
     * @return valus as long
     * @throws DataTypeException
     * @throws IllegalArgumentException
     */
    public long getLong(String name) throws DataTypeException, IllegalArgumentException {
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            long l = (Integer)get(name);
            return l;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to long.");
        }
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
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            String s = (String)get(name);
            return s;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to String.");
        }
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
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            Instant instant = Instant.parse((String)get(name));
            Date d = new Date(instant.toEpochMilli());
            return d;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to Date.");
        }
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
        if(!isNameValid(name)) {
            throw new IllegalArgumentException(name + " is not a valid column name in this row.");
        }
        try {
            UUID uuid = UUID.fromString((String)get(name));
            return uuid;
        } catch(ClassCastException ex) {
            throw new DataTypeException("Can't cast value to Date.");
        }
    }
    
    private boolean isNameValid(String name) throws IllegalArgumentException {
        return (row.keySet().contains(name) || row.keySet().contains(name.substring(0, name.lastIndexOf("_data"))));
    }

    /**
     * Pronts out the row columns and their values.
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
