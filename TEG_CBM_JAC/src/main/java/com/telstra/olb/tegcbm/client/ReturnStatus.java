/*
 * ReturnStatus.java
 * Created on 25/07/2007 by pavan.x.kuma
 */
package com.telstra.olb.tegcbm.client;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.telstra.olb.types.AbstractEnum;

/**
 * Enum to represent Return Status of execution.
 */
public final class ReturnStatus extends AbstractEnum {
    
    public static final ReturnStatus SUCCESS = new ReturnStatus("success");
    public static final ReturnStatus FAILURE = new ReturnStatus("failure");
    public static final ReturnStatus INVALID_ARGS = new ReturnStatus("INVALID_ARGS");
    
    private static int nextOrdinal = 0;
    private static final AbstractEnum[] TYPES = {SUCCESS,  FAILURE, INVALID_ARGS};

    private static Map nameMap = new HashMap();
    private static Map valueMap = new HashMap();
    
    /**
     * get type from name. 
     * @param name to get
     * @return instance of ReturnStatus
     */
    public static ReturnStatus getType(String name) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (ReturnStatus) getType(nameMap,name);
    }

    /**
     * get type by value. 
     * @param value to get
     * @return instance of ReturnStatus
     */
    public static ReturnStatus getType(int value) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (ReturnStatus) getType(valueMap,value);
    }
    /**
     * 
     * @param name return status name
     */
    private ReturnStatus(String name) {
        super(name, nextOrdinal++);
    }

    /**
     * Allows this class to resolve the object read from the stream before it is returned to the caller.
     * @return instance of type
     * @throws ObjectStreamException -
     */
    private Object readResolve() throws ObjectStreamException {
        return TYPES[getValue()];
    }
}
