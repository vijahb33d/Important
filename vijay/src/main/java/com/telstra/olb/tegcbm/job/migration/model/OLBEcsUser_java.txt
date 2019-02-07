/*
 * Created on 7/05/2009
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
public class OLBEcsUser {
	
	private String userId;
	private int attrId;
	private List accounts = new ArrayList();
	private Map preferences =  new HashMap();
	
	public OLBEcsUser(){
		
	}
	
	public OLBEcsUser(String userId, int attrId, List accounts){
		this.userId = userId;
		this.attrId = attrId;
		this.accounts = accounts;
	}

	/**
	 * @return Returns the accounts.
	 */
	public List getAccounts() {
		return accounts;
	}
	/**
	 * @param accounts The accounts to set.
	 */
	public void setAccounts(List accounts) {
		this.accounts = accounts;
	}
	/**
	 * @return Returns the preferences.
	 */
	public Map getPreferences() {
		return preferences;
	}
	/**
	 * @param preferences The preferences to set.
	 */
	public void setPreferences(Map preferences) {
		this.preferences = preferences;
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
