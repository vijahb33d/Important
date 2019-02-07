/*
 * Created on 6/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OLBTBUnmanagedCompanyPreference {
	
	private OLBTBUnmanagedCompanyMigration company;
	private String userId;
	private String preference;
	private String preferenceValue;
	private List accountList = new ArrayList();
	private Map ecsUsers = new HashMap();
	private int attrId;
	
	/**
	 * @return Returns the accountList.
	 */
	public List getAccountList() {
		return accountList;
	}
	/**
	 * @param accountList The accountList to set.
	 */
	public void setAccountList(List accountList) {
		this.accountList = accountList;
	}
	/**
	 * @return Returns the preference.
	 */
	public String getPreference() {
		return preference;
	}
	/**
	 * @param preference The preference to set.
	 */
	public void setPreference(String preference) {
		this.preference = preference;
	}
	/**
	 * @return Returns the preferenceValue.
	 */
	public String getPreferenceValue() {
		return preferenceValue;
	}
	/**
	 * @param preferenceValue The preferenceValue to set.
	 */
	public void setPreferenceValue(String preferenceValue) {
		this.preferenceValue = preferenceValue;
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
	 * @return Returns the company.
	 */
	public OLBTBUnmanagedCompanyMigration getCompany() {
		return company;
	}
	/**
	 * @param company The company to set.
	 */
	public void setCompany(OLBTBUnmanagedCompanyMigration company) {
		this.company = company;
	}
	/**
	 * Method returns the Ecs user object for the userId
	 * @param userId
	 * @return
	 */
	public OLBEcsUser getEcsUser(String userId){
		if(userId != null && this.getEcsUsers() != null) {
			return (OLBEcsUser)this.ecsUsers.get(userId);
		}
		return null;
	}
	/**
	 * @return Returns the ecsUsers.
	 */
	public Map getEcsUsers() {
		return ecsUsers;
	}
	/**
	 * @param ecsUsers The ecsUsers to set.
	 */
	public void setEcsUsers(Map ecsUsers) {
		this.ecsUsers = ecsUsers;
	}
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
}
