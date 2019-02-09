/*
 * OwnCodeCategory.java
 * Created on 15/08/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Value Object that holds the segment information for an own code.
 */

public class OwnCodes {
    /**
     * map of won codes to their segments.
     */
    private Map ownCodeSegmentMap = new HashMap();
    /**
     * adds an entry into a map that holds the own code and segment information.
     *
     * @param ownCode own code
     * @param segment segment
     */
    public void put(String ownCode, String segment) {
        ownCodeSegmentMap.put(ownCode, segment);
    }
    /**
     * gets the segment for the own code. 
     *
     * @param ownCode owncode
     * @return segment.
     */
    public String getSegment(String ownCode) {
        return (String) ownCodeSegmentMap.get(ownCode);
    }
    
    /**
     * returns the complete set of own codes. 
     *
     * @return owncodes set.
     */
    public Set getOwnCodes() {
        return ownCodeSegmentMap.keySet();
    }
    /**
     *
     * @return the size of own codes set.
     */
    public int size() {
        return ownCodeSegmentMap.keySet().size();
    }
}
