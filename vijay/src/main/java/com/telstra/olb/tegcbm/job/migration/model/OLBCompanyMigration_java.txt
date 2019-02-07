/*
 * OLBCompanyMigration.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Hibernate class for OLB_COMPANY_MIGRATION table in the datastore.
 * The mapping is defined in OLBCompanyMigration.hbm.xml
 */
public class OLBCompanyMigration implements Serializable {
    
	private String companyCode;
    
    private String segment;
    
    private String ownCode;
    
    private int activityStatus;
    
    private Date createdDate;
    
    private Date updatedDate;
    
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
     * @return Returns the updatedDate.
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }
    /**
     * @param updatedDate The updatedDate to set.
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
    /**
     * @return Returns the ownCode.
     */
    public String getOwnCode() {
        return ownCode;
    }
    /**
     * @param ownCode The ownCode to set.
     */
    public void setOwnCode(String ownCode) {
        this.ownCode = ownCode;
    }
    /**
     * @return Returns the segment.
     */
    public String getSegment() {
        return segment;
    }
    /**
     * @param segment The segment to set.
     */
    public void setSegment(String segment) {
        this.segment = segment;
    }
    /**
     * @return Returns the status.
     */
    public int getActivityStatus() {
        return activityStatus;
    }
    /**
     * @param status The status to set.
     */
    public void setActivityStatus(int status) {
        this.activityStatus = status;
    }
    /**
     * Returns the string representation for the current object.
     * 
     * @return string representation of this object.
     */
    public String toString() {
        return companyCode + "[" + segment + "," + ownCode + "]";
    }
    /**
     * HashCode method implementation.
     * @return hashcode of company identifier.
     */
    public int hashCode() {
		return companyCode.hashCode();
	}
	/**
	 * Equals method implementation.
	 * @param obj company migration object.
	 * @return true if the obj company code is same as this object.
	 */
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    OLBCompanyMigration other = (OLBCompanyMigration) obj;
		return other.companyCode.equals(companyCode);
	}
}
