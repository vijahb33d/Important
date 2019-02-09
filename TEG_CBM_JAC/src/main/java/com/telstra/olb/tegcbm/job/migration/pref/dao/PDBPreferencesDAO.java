package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.util.List;

/**
 * DAO Interface to PDB User Account Preferences table.
 */
public interface PDBPreferencesDAO {

    /**
     * Retrieves the User Account preferences from the PDB datastore.
     * 
	 * @param cidn Company cidn
	 * @return list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    List getUserAccountPreferencesByCIDN(String cidn) throws PDBPreferencesDAOException;
    
   /**
    * Updates the SPP preferences for the user.
    * 
    * @param userAccountPreferences list of user notifictaion preferences
    * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
    */
    void processSPP(List userAccountPreferences) throws PDBPreferencesDAOException;
    
    /**
     * Removes the user account preferences from the PDB database.
     * 
     * @param userAccountPreferences list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    void deleteUserAccountPreferences(List userAccountPreferences) throws PDBPreferencesDAOException;
    
    /**
     * Creates the backup copy of the user account preferences data in the new backup table.
     * 
     * @param cidn Company cidn
     * @param userAccountPreferences list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    void backupUserAccountPreference(String cidn, List userAccountPreferences) throws PDBPreferencesDAOException;
    
}
