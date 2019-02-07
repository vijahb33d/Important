package com.telstra.olb.tegcbm.job.migration.pref.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesRollbackDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesRollbackDAOException;

/**
 * This class provides the functionality for the Preferences clean up activity.
 * It removes the preferences stored in CBM if any for the CIDN.
 */
public class PreferencesCleanupActivity extends DefaultActivity {

    private static Log log = LogFactory.getLog(PreferencesCleanupActivity.class);

    private CBMPreferencesRollbackDAO cbmPreferencesRollbackDao;
        
    /**
      * Method defining the functionality for the preferences cleanup activity.
      * 
      * 
      * @param input OLBCompanyMigration information
      * @throws ActivityException is thrown if the preferences could not be removed.
      */
    public void doExecute(Object  input) throws ActivityException {
    	OLBCompanyMigration company = (OLBCompanyMigration) input;
        try {
            if (log.isDebugEnabled()) { log.debug("Removing User prefrences from CBM: " + company.getCompanyCode());}
    		cbmPreferencesRollbackDao.removeUserPreferences(company.getCompanyCode());
    		if (log.isDebugEnabled()) { log.debug("Preferences data successfully removed from CBM for cidn:" + company.getCompanyCode());}
		} catch (CBMPreferencesRollbackDAOException e) {
			throw new ActivityException("Problem cleaning up preferences in cbm for company:" + company.getCompanyCode(), e);
		}
    }
	/**
	 * 
	 * @param cbmPreferencesRollbackDao The cbmPreferencesRollbackDao to set.
	 */
	public void setCbmPreferencesRollbackDao(CBMPreferencesRollbackDAO cbmPreferencesRollbackDao) {
		this.cbmPreferencesRollbackDao = cbmPreferencesRollbackDao;
	}
}
