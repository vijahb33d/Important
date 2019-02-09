/*
 * Created on 8/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBMUserPreferenceBackup implements Serializable{
		
	private int id;
	private int attrId;
	private String attrVal;
	private String attrKey;
	private Date createdDate;
	private String companyCode;

	/**
	 * @return Returns the attrId.
	 */
	public int getAttrId() {
		return attrId;
	}
	/**
	 * @param attrId The attrId to set.
	 */
	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}
	/**
	 * @return Returns the attrKey.
	 */
	public String getAttrKey() {
		return attrKey;
	}
	/**
	 * @param attrKey The attrKey to set.
	 */
	public void setAttrKey(String attrKey) {
		this.attrKey = attrKey;
	}
	/**
	 * @return Returns the attrVal.
	 */
	public String getAttrVal() {
		return attrVal;
	}
	/**
	 * @param attrVal The attrVal to set.
	 */
	public void setAttrVal(String attrVal) {
		this.attrVal = attrVal;
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
	 * @return Returns the createdDate.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate The createdDate to set.
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
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
}
