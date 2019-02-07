/*
 * MigrationType.java
 * Created on 15/08/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.model;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;
import com.telstra.olb.types.AbstractEnum;

/**
 * Migration Type abstraction for Populate Activity. Since we need to get all the companies based on segments, 
 * this abstraction provides a way of controlling which segments need to be retrieved. Based on the type of 
 * abstraction, we retrieve the own codes for list of segments associated with the migration type.
 * <p>
 * At present, the two types of migrations supported are
 * <li> ENTERPRISE - list of segments associated with ithis type are Key-Corporate &amp; International </li>
 * <li>SME - list of segments associated with ithis type are SmallBusiness &amp; SME </li>
 * </p>
 */

public final class MigrationType extends AbstractEnum {
    public static final MigrationType ENTERPRISE = new MigrationType("ENTERPRISE", "Key-Corporate,International");
    public static final MigrationType SME = new MigrationType("SME", "SmallBusiness,SME");
    public static final MigrationType TBU = new MigrationType("TBU", "SmallBusiness");
    
    private static final AbstractEnum[] TYPES = {SME, ENTERPRISE, TBU};

    private static Map nameMap = new HashMap();
    private static Map valueMap = new HashMap();
    private static int nextOrdinal = 0;
    
    
    /**
     * get type from name. 
     * @param name to get
     * @return instance of MigrationType
     */
    public static MigrationType getType(String name) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (MigrationType) getType(nameMap,name);
    }

    /**
     * get type by value. 
     * @param value to get
     * @return instance of MigrationType
     */
    public static MigrationType getType(int value) {
        if (nameMap.isEmpty()) {
        	map(TYPES,nameMap,valueMap);
        }
        return (MigrationType) getType(valueMap,value);
    }
    
    private List segments;
    
    /**
     * Migration Type Ctor.
     * @param name migration type
     * @param values list of segments that constitute the type.
     */
    private MigrationType(String name, String values) {
        super(name, nextOrdinal++);
        segments = MigrationHelper.parse(values, ",");
    }
    
    /**
     * Allows this class to resolve the object read from the stream before it is returned to the caller.
     * @return instance of type
     * @throws ObjectStreamException -
     */
    private Object readResolve() throws ObjectStreamException {
        return TYPES[getValue()];
    }
    
    /**
     * @return Returns the segments.
     */
    public List getSegments() {
        return segments;
    }
}
