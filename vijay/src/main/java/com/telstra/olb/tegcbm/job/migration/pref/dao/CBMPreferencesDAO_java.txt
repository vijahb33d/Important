package com.telstra.olb.tegcbm.job.migration.pref.dao;

import com.telstra.olb.tegcbm.job.migration.pref.model.UserNotification;

/**
 * DAO Interface to CBM Preferences table.
 * 
 * @author d274681
 */
public interface CBMPreferencesDAO {

	/**
	 * Reads the user notification settings and stores them in the CBM database.
	 * 
	 * @param userNotification User's notification settings.
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM datastore
	 */
	void updateUserPreferences(UserNotification userNotification) throws CBMPreferencesDAOException;
}
