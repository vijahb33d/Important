/*
 * Created on Feb 4, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.model;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HierarchyNode {

    private long nodeId;

    private long hierarchyId;

    /**
     * @param hid
     * @param nodeId
     */
    public HierarchyNode(long hid, long nodeId) {
        this.hierarchyId = hid;
        this.nodeId = nodeId;
    }

    /**
     * @return Returns the hierarchyId.
     */
    public long getHierarchyId() {
        return hierarchyId;
    }

    /**
     * @param hierarchyId The hierarchyId to set.
     */
    public void setHierarchyId(long hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    /**
     * @return Returns the nodeId.
     */
    public long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId The nodeId to set.
     */
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }
}
