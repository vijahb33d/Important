package com.telstra.olb.tegcbm.job.migration.pref.activity;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesRollbackDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesRollbackDAOException;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesRollbackDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesRollbackDAOException;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.HierarchyCleanUpDAO;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.HierarchyCleanUpException;

/**
 * This class provides the functionality for the Preferences Roll back activity.
 * It roll backs the updates that have been performed to the data store
 * as a result of execution of User notification preferences migration.
 */
public class PreferencesRollbackActivity extends DefaultActivity {

    private static Log log = LogFactory.getLog(PreferencesRollbackActivity.class);

    private PDBPreferencesRollbackDAO preferencesDao;
    
    private CBMPreferencesRollbackDAO cbmPreferencesRollbackDao;
    
    private HierarchyCleanUpDAO hierarchyCleanUpDAO;
    
    /**
      * Method defining the functionality for the preferences roll back activity.
      * Retrieves information from the back up table and stores it back to the 
      * main table.
      * 
      * @param input OLBCompanyMigration information
      * @throws ActivityException is thrown if the preferences could not be rolledback.
      */
    public void doExecute(Object  input) throws ActivityException {
    	OLBCompanyMigration company = (OLBCompanyMigration) input;
        try {
            if (log.isDebugEnabled()) { log.debug("Restoring backed up preferences from backup table for: " + company.getCompanyCode());}
            hierarchyCleanUpDAO.deleteAccountCompanyEntriesForCIDN(company.getCompanyCode());
            List preferences = preferencesDao.getBackedUpUserAccountPreferencesByCIDN(company.getCompanyCode());
    		preferencesDao.restoreUserPreferencesToPDB(preferences);
    		cbmPreferencesRollbackDao.removeUserPreferences(company.getCompanyCode());
    		preferencesDao.removeBackedUpUserAccountPreference(company.getCompanyCode());
            if (log.isDebugEnabled()) { log.debug("Preferences data successfully removed from PDB and CBM for cidn:" + company.getCompanyCode());}
		} catch (PDBPreferencesRollbackDAOException e) {
			throw new ActivityException("Problem rolling back preferences from pdb for company:" + company.getCompanyCode(), e);
		} catch (CBMPreferencesRollbackDAOException e) {
			throw new ActivityException("Problem rolling back preferences from cbm for company:" + company.getCompanyCode(), e);
		} catch (HierarchyCleanUpException e) {
			throw new ActivityException("Problem rolling back preferences from cbm for company:" + company.getCompanyCode(), e);
		}
    }
    /**
     * @param preferencesDao The preferencesDao to set.
     */
    public void setPreferencesDao(PDBPreferencesRollbackDAO preferencesDao) {
        this.preferencesDao = preferencesDao;
    }
	/**
	 * 
	 * @param cbmPreferencesRollbackDao The cbmPreferencesRollbackDao to set.
	 */
	public void setCbmPreferencesRollbackDao(
			CBMPreferencesRollbackDAO cbmPreferencesRollbackDao) {
		this.cbmPreferencesRollbackDao = cbmPreferencesRollbackDao;
	}
	/**
	 * @return Returns the hierarchyCleanUpDAO.
	 */
	public HierarchyCleanUpDAO getHierarchyCleanUpDAO() {
		return hierarchyCleanUpDAO;
	}
	/**
	 * @param hierarchyCleanUpDAO The hierarchyCleanUpDAO to set.
	 */
	public void setHierarchyCleanUpDAO(HierarchyCleanUpDAO hierarchyCleanUpDAO) {
		this.hierarchyCleanUpDAO = hierarchyCleanUpDAO;
	}
}
