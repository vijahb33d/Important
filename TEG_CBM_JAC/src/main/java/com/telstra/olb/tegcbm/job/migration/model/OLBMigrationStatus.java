/*
 * OLBCompanyMigration.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;

/**
 * Hibernate class for OLB_MIGRATION_STATUS table in the datastore.
 * The mapping is defined in OLBMigrationStatus.hbm.xml
 */
public class OLBMigrationStatus implements Serializable {
    
	private int id;
    
    private String status;
    
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
	 * @return Returns the status.
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
