/*
 * Created on 7/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.io.Serializable;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PDBUserAccountPreference implements Serializable{
	
	private String userId;
	private String accountNumber;
	private String applicationId;
	private String attributeId;
	private String value;
	private char migrated;
	private String contacts;
	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	/**
	 * @param accountNumber The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	/**
	 * @return Returns the applicationId.
	 */
	public String getApplicationId() {
		return applicationId;
	}
	/**
	 * @param applicationId The applicationId to set.
	 */
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	/**
	 * @return Returns the attributeId.
	 */
	public String getAttributeId() {
		return attributeId;
	}
	/**
	 * @param attributeId The attributeId to set.
	 */
	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	/**
	 * @return Returns the contacts.
	 */
	public String getContacts() {
		return contacts;
	}
	/**
	 * @param contacts The contacts to set.
	 */
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	/**
	 * @return Returns the migrated.
	 */
	public char getMigrated() {
		return migrated;
	}
	/**
	 * @param migrated The migrated to set.
	 */
	public void setMigrated(char migrated) {
		this.migrated = migrated;
	}
	/**
	 * @return Returns the userId.
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
