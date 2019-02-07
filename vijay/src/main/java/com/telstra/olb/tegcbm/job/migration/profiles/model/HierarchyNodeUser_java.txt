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
public class HierarchyNodeUser {
	
	private long nodeId;
	private long userId;

	/**
	 * @param nodeid2
	 * @param userId2
	 */
	public HierarchyNodeUser(long nodeid, long userId) {
		this.nodeId=nodeid;
		this.userId=userId;
		
		// TODO Auto-generated constructor stub
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
	/**
	 * @return Returns the userId.
	 */
	public long getUserId() {
		return userId;
	}
	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}
}
