/*
 * Created on Feb 3, 2010
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
public class Hierarchy {
	
	private long id;
	private long hierarchyType;
	private String name;
	private long deletedAt;
	
	

	
	
	/**
	 * @param id2
	 * @param htype
	 * @param name2
	 */
	public Hierarchy(long id, long htype, String name) {
		
		this.id=id;
		this.hierarchyType=htype;
		this.name=name;
		
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
	/**
	 * @return Returns the deletedAt.
	 */
	public long getDeletedAt() {
		return deletedAt;
	}
	/**
	 * @param deletedAt The deletedAt to set.
	 */
	public void setDeletedAt(long deletedAt) {
		this.deletedAt = deletedAt;
	}
	/**
	 * @return Returns the hierarchyType.
	 */
	public long getHierarchyType() {
		return hierarchyType;
	}
	/**
	 * @param hierarchyType The hierarchyType to set.
	 */
	public void setHierarchyType(long hierarchyType) {
		this.hierarchyType = hierarchyType;
	}
	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
}
