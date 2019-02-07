package com.telstra.olb.tegcbm.job.core;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.telstra.olb.types.AbstractEnum;

/**
 * Enum to represent the status of an Activity execution.
 */
public final class ActivityStatus extends AbstractEnum {
    
    public static final ActivityStatus STATUS_PENDING = new ActivityStatus(1, "pending");
    public static final ActivityStatus STATUS_PROCESSING = new ActivityStatus(2, "processing");
    public static final ActivityStatus STATUS_COMPLETE = new ActivityStatus(3, "complete");
    public static final ActivityStatus STATUS_ERROR = new ActivityStatus(4, "error");
    public static final ActivityStatus STATUS_ROLLBACK = new ActivityStatus(5, "rollback");

    private static final AbstractEnum[] TYPES = {STATUS_PENDING, STATUS_PROCESSING, STATUS_COMPLETE,  STATUS_ERROR};

    private static Map nameMap = new HashMap();
    private static Map valueMap = new HashMap();
    
    /**
     * get type from name. 
     * @param name to get
     * @return instance of ActivityStatus
     */
    public static ActivityStatus getType(String name) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (ActivityStatus) getType(nameMap,name);
    }

    /**
     * get type by value. 
     * @param value to get
     * @return instance of ActivityStatus
     */
    public static ActivityStatus getType(int value) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (ActivityStatus) getType(valueMap,value);
    }
    /**
     * 
     * @param id activity id
     * @param name activity name
     */
    private ActivityStatus(int id, String name) {
        super(name, id);
    }

    /**
     * Allows this class to resolve the object read from the stream before it is returned to the caller.
     * @return instance of type
     * @throws ObjectStreamException -
     */
    private Object readResolve() throws ObjectStreamException {
        return TYPES[getValue() - 1];
    }

}
