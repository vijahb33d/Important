/*
 * TaskStatus.java
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.concurrency;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.telstra.olb.types.AbstractEnum;

/**
 * Enum representing the status of a task.
 */
public final class TaskStatus extends AbstractEnum {
    public static final TaskStatus TASK_SUCCESS = new TaskStatus("success");
    public static final TaskStatus TASK_FAILURE = new TaskStatus("failure");
    
    private static int nextOrdinal = 0;
    private static final AbstractEnum[] TYPES = {TASK_SUCCESS,  TASK_FAILURE};

    private static Map nameMap = new HashMap();
    private static Map valueMap = new HashMap();
    
    /**
     * get type from name. 
     * @param name to get
     * @return instance of TaskStatus
     */
    public static TaskStatus getType(String name) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (TaskStatus) getType(nameMap,name);
    }

    /**
     * get type by value. 
     * @param value to get
     * @return instance of TaskStatus
     */
    public static TaskStatus getType(int value) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (TaskStatus) getType(valueMap,value);
    }
    /**
     * 
     * @param name task status name
     */
    private TaskStatus(String name) {
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
