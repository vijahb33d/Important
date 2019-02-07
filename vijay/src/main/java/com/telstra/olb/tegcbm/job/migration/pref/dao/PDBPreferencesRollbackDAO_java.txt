package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.util.List;

/**
 * DAO Interface to PDB user-account Preferences table for rollback.
 *
 */
public interface PDBPreferencesRollbackDAO {

    /**
     * Retrieves the User Account preferences from the PDB datastore backup table.
     * 
	 * @param cidn Company Id
	 * @return List of UserAccountPreferences
	 * @throws PDBPreferencesRollbackDAOException upon exception
     */
    List getBackedUpUserAccountPreferencesByCIDN(String cidn) throws PDBPreferencesRollbackDAOException;
    /**
     * Removes the user account preferences from the PDB database.
     * 
     * @param userAccountPreferences list of user account preferences
     * @throws PDBPreferencesRollbackDAOException upon exception
     */
    void restoreUserPreferencesToPDB(List userAccountPreferences) throws PDBPreferencesRollbackDAOException;
    /**
     * Creates the backup copy of the user account preferences data in the new backup table.
     * @param cidn
     * 
     * @param cidn Company Id
	 * @throws PDBPreferencesRollbackDAOException upon exception
     */
    void removeBackedUpUserAccountPreference(String cidn) throws PDBPreferencesRollbackDAOException;
}
