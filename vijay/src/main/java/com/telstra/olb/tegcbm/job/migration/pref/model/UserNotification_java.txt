package com.telstra.olb.tegcbm.job.migration.pref.model;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the notification settings for the user.
 * 
 * @author d274681
 */
public class UserNotification {

	private String ecsId;
	private Set notificationTypes;
	
	/**
	 * @return Returns the ecsId.
	 */
	public String getEcsId() {
		return ecsId;
	}
	/**
	 * @param ecsId The ecsId to set.
	 */
	public void setEcsId(String ecsId) {
		this.ecsId = ecsId;
	}
	/**
	 * @return Returns the notificationTypes.
	 */
	public Set getNotificationTypes() {
		if (notificationTypes == null) {
			notificationTypes = new HashSet();
		}
		return notificationTypes;
	}
	/**
	 * @param notificationTypes The notificationTypes to set.
	 */
	public void setNotificationTypes(Set notificationTypes) {
		this.notificationTypes = notificationTypes;
	}
	/**
	 * Checks if the user has email notification preference set.
	 * Persists it to the database if it is so.
	 * 
	 * @param userPreference user notification preference
	 */
	public void updateUserNotificationPreference(UserAccountPreference userPreference) {
	    if (!getNotificationTypes().contains(userPreference.getAttributeId())) {
			// REVIEW: DF does this imply only an email preference is migrated?
	        // this is incorrect - any preference, email or SMS, will result in a migration.
	        if ( (!(userPreference.getValue().indexOf("email") == -1))
	        		|| (!(userPreference.getValue().indexOf("sms") == -1))) {
				getNotificationTypes().add(userPreference.getAttributeId());
			}
		}
	    
	}
}
