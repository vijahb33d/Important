package com.telstra.olb.tegcbm.job.migration.pref.dao;


/**
 * DAO Interface to CBM Preferences table. Performs the rollback
 * operations for preferences.
 * 
 * @author d274681
 */
public interface CBMPreferencesRollbackDAO {

	/**
	 * Removes user notification preferences for all the users for a company from the CBM datastore.
	 * 
	 * @param cidn Company CIDN.
     * @throws CBMPreferencesRollbackDAOException thrown if there is problem working with CBM datastore
	 */
	void removeUserPreferences(String cidn) throws CBMPreferencesRollbackDAOException;
}
