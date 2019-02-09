/*
 * OLBCompanyMigration.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;

/**
 * Hibernate class for OLB_MIGRATION_ACTIVITY table in the datastore.
 * The mapping is defined in OLBMigrationActivity.hbm.xml
 */
public class OLBMigrationActivity implements Serializable {
    
	private int id;
    
    private String activityName;
    
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return Returns the activityName.
	 */
	public String getActivityName() {
		return activityName;
	}
	/**
	 * @param activityName The activityName to set.
	 */
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
}
