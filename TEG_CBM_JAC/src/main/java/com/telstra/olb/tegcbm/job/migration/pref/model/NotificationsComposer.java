package com.telstra.olb.tegcbm.job.migration.pref.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class reads the list of Notification preferences and groups them 
 * into unique notification settings.
 * 
 * @author d274681
 */
public class NotificationsComposer {
	
    private static Log log = LogFactory.getLog(NotificationsComposer.class);

	/**
	 * Group the user preferences as the map of user ecsid and it's list of preferences.
	 * 
	 * @param preferences Collection of all UserAccountPreference objects for the particular company
	 * @return Collection collection of UserNotification objects
	 */
	public Collection composeUserPreferences(List preferences) {
        if (log.isDebugEnabled()) { log.debug("Grouping user preferences");}
		Map map = new HashMap();
		// loop thru all the user preferences and stores them in a map as userid key and its preferences as set
		for (Iterator it = preferences.iterator(); it.hasNext();) {
			// get the user preference
			UserAccountPreference userPreference = (UserAccountPreference)it.next();
			handlePreference(userPreference, map);
		}		
		return map.values();
	}

	/**
	 * Stores the notification settings to the map.
	 * 
	 * @param userPreference user notification preference
	 * @param preferencesMap notification preferences map
	 */
	private void handlePreference(UserAccountPreference userPreference, Map preferencesMap) {
		UserNotification userNotification = (UserNotification) preferencesMap.get(userPreference.getUserId());
		if (userNotification == null) {
		    userNotification = new UserNotification();
		    userNotification.setEcsId(userPreference.getUserId());
		    preferencesMap.put(userPreference.getUserId(), userNotification);
		}
		userNotification.updateUserNotificationPreference(userPreference);
	}

}
