package com.telstra.olb.tegcbm.job.migration.profiles.dao;

import java.util.Collection;

/**
 * Data Access Layer to access PDB to provide the following profile management
 * functionality.
 * <li>get the accounts that are enrolled for a customer</li>
 * <li>gets the profile cache time out information</li>
 * <li>sets the profile cache time out to a value (in hrs)</li>
 *
 */
public interface PDBProfilesDAO {

	/**
	 * Finds the accounts for the passed cidn in the pdb database and returns as a List.
	 *  
	 * @param cidn Company cidn to search account for
	 * @return List collection of accounts
	 * @throws PDBProfilesDAOException is thrown if the accounts cannot be retrieved.
	 */
	Collection getAccountsForCompany(String cidn) throws PDBProfilesDAOException;
	
	/**
	 * 
	 * Sets the profile cache timeout to control R&E updates.
	 *
	 * @param timeout timeout in hours
	 * @throws PDBProfilesDAOException is thrown if the timeout values cannot be set.
	 */
	void setProfileCacheTimeOut(long timeout) throws PDBProfilesDAOException;
	
	/**
	 * gets the current profile cache timeout. 
	 *
	 * @return timeout in hours.
	 * @throws PDBProfilesDAOException is thrown if the timeout value cannot be retrieved.
	 */
	long getProfileCacheTimeOut() throws PDBProfilesDAOException;
}
