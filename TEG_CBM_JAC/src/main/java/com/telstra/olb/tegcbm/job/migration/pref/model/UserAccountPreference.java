package com.telstra.olb.tegcbm.job.migration.pref.model;

/**
 * Value object to hold a User Account preference.
 * 
 * @author daniel.fajerman
 */
public class UserAccountPreference {

	private String userId;
    private String accountNumber;
    private String appId;
    private String attributeId;
    private String value;
   
    /**
     * UserAccountPreference constructor.
     * 
     * @param userId User Ecsid
     * @param accountNumber Account Number
     * @param appId Application Id
     * @param attributeId Preference type
     * @param value Preference value
     */
    public UserAccountPreference(String userId, String accountNumber, 
    		String appId, String attributeId, String value) {
    	this.userId = userId;
    	this.accountNumber = accountNumber;
    	this.appId = appId;
    	this.attributeId = attributeId;
    	this.value = value;
    }
    /**
     * @return Returns the appId.
     */
    public String getAppId() {
        return appId;
    }
    /**
     * @param appId The appId to set.
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }
    /**
     * @return Returns the type.
     */
    public String getAttributeId() {
        return attributeId;
    }
    /**
     * @param type The type to set.
     */
    public void setAttributeId(String type) {
        this.attributeId = type;
    }
    /**
     * @return Returns the userId.
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    /**
     * @param userId The userId to set.
     */
    public void setAccountNumber(String userId) {
        this.accountNumber = userId;
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
}
