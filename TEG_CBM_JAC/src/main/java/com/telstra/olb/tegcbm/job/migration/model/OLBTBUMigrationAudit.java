/*
 * OLBCompanyMigration.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Hibernate class for OLB_MIGRATION_AUDIT table in the datastore.
 * The mapping is defined in OLBMigrationAudit.hbm.xml
 */
public class OLBTBUMigrationAudit implements Serializable {
    private int id;

	private String companyCode;
    
    private OLBMigrationActivity activity;
    
    private OLBMigrationStatus activityStatus;
    
    private Date createdDate;
    
    private Date updatedDate;
    
    private String comments;
    
    
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
	 * @return Returns the activity.
	 */
	public OLBMigrationActivity getActivity() {
		return activity;
	}
	/**
	 * @param activity The activity to set.
	 */
	public void setActivity(OLBMigrationActivity activity) {
		this.activity = activity;
	}
	/**
	 * @return Returns the activityStatus.
	 */
	public OLBMigrationStatus getActivityStatus() {
		return activityStatus;
	}
	/**
	 * @param activityStatus The activityStatus to set.
	 */
	public void setActivityStatus(OLBMigrationStatus activityStatus) {
		this.activityStatus = activityStatus;
	}
	/**
	 * @return Returns the companyCode.
	 */
	public String getCompanyCode() {
		return companyCode;
	}
	/**
	 * @param companyCode The companyCode to set.
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	/**
	 * @return Returns the created.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param created The created to set.
	 */
	public void setCreatedDate(Date created) {
		this.createdDate = created;
	}
	/**
	 * @return Returns the updated.
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	/**
	 * @param updated The updated to set.
	 */
	public void setUpdatedDate(Date updated) {
		this.updatedDate = updated;
	}
    /**
     * @return Returns the comments.
     */
    public String getComments() {
        return comments;
    }
    /**
     * @param comments The comments to set.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
}
