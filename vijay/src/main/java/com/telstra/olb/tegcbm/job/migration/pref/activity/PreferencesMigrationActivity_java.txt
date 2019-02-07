package com.telstra.olb.tegcbm.job.migration.pref.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesDAOException;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesDAOException;
import com.telstra.olb.tegcbm.job.migration.pref.model.NotificationsComposer;
import com.telstra.olb.tegcbm.job.migration.pref.model.UserAccountPreference;
import com.telstra.olb.tegcbm.job.migration.pref.model.UserNotification;

/**
 * This class provides the functionality for the Notification preferences
 * data migration from PDB data store to CBM data store.
 */
public class PreferencesMigrationActivity extends DefaultActivity {

    private PDBPreferencesDAO preferencesDao;
    private CBMPreferencesDAO cbmPreferencesDao;
    private MigrationDAO migrationDao;
    private static Log log = LogFactory.getLog(PreferencesMigrationActivity.class);
    
    /**
     * Method defining the functionality for the preferences migration activity.
     * 
     * @param input OLBCompanyMigration information
     * @throws ActivityException thrown if there is problem migrating preferences
     */
    public void doExecute(Object input) throws ActivityException {
    	OLBCompanyMigration current = (OLBCompanyMigration) input;
        try {
			migrate(current.getCompanyCode());
		} catch (PDBPreferencesDAOException e) {
			throw new ActivityException("Problem migrating preferences for company code:" + current.getCompanyCode(), e);
		} catch (CBMPreferencesDAOException e) {
			throw new ActivityException("Problem migrating preferences for company code:" + current.getCompanyCode(), e);
		}
    }
    
   
    /**
     * Main method performing the migration of notifications.
     * 
     * @param cidn Company code
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB data store
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
     */
    protected void migrate(String cidn) throws PDBPreferencesDAOException, CBMPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Migrating preferences for cidn: " + cidn);}
        List preferences = new ArrayList();
        if (log.isDebugEnabled()) { log.debug("Retrieve preferences from pdb");}
		preferences = preferencesDao.getUserAccountPreferencesByCIDN(cidn);
		backupPDBPreferences(cidn, preferences);
		storeUserPreferencesToCBM(preferences);
		processSPP(preferences);
        removePDBPreferences(preferences);
    }

    /**
     * Stores the user notification preferences in the back up table before
     * performing any operations on the main data.
     * 
	 * @param cidn Company Cidn
     * @param preferences list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
	 */
	private void backupPDBPreferences(String cidn, List preferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Backup preferences to pdb table");}
		preferencesDao.backupUserAccountPreference(cidn, preferences);
	}

    /**
     * Migrates user notification preferences to CBM data store.
     * 
	 * @param preferences list of notification preferences to be migrated
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
	 */
	private void storeUserPreferencesToCBM(List preferences) throws CBMPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Migrate preferences to cbm");}
		Collection allUsersPreferences = groupUserPreferences(preferences);
		for (Iterator it = allUsersPreferences.iterator(); it.hasNext();) {
			UserNotification userNotification = (UserNotification)it.next();
            createCBMPreferences(userNotification);
		}
	}

	/**
	 * Group the user notification preferences uniquely.
	 * 
	 * @param preferences list of UserAccountPreferences
	 * @return Collection collection of UserPreference objects
	 */
	private Collection groupUserPreferences(List preferences) {
		NotificationsComposer composer = new NotificationsComposer();
		return composer.composeUserPreferences(preferences);
	}

	/**
	 * Store the preferences to CBM data store.
	 * 
	 * @param userNotification user's notification preferences
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
	 */
	protected void createCBMPreferences(UserNotification userNotification) throws CBMPreferencesDAOException {
		cbmPreferencesDao.updateUserPreferences(userNotification);
    }

    /**
     * Removes the user notification preferences from PDB data store.
     * 
     * @param preferences list of preferences to be removed from PDB
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB data store
     */
    protected void removePDBPreferences(List preferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Remove pdb preferences after migration");}
        //remove BAN, BPR, LPR preferences from PDB
        preferencesDao.deleteUserAccountPreferences(preferences);
    }

    /**
     * Process the Scheduled Payment Paid notification.
     * 
     * @param preferences list of notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB data store
     */
    protected void processSPP(List preferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Process spp preferences");}
        List sppPreferences = new ArrayList();
        CollectionUtils.select(preferences, new Predicate(){
	            public boolean evaluate(Object userAccountPreference) {
	                UserAccountPreference pref = (UserAccountPreference) userAccountPreference;
	                return pref.getValue().equals("SPP");
	            }
            }, sppPreferences);
    	preferencesDao.processSPP(sppPreferences);
    }

    /**
     * @param preferencesDao
     *            The preferencesDao to set.
     */
    public void setPreferencesDao(PDBPreferencesDAO preferencesDao) {
        this.preferencesDao = preferencesDao;
    }
	/**
	 * @return Returns the cbmPreferencesDao.
	 */
	public CBMPreferencesDAO getCbmPreferencesDao() {
		return cbmPreferencesDao;
	}
	/**
	 * @param cbmPreferencesDao The cbmPreferencesDao to set.
	 */
	public void setCbmPreferencesDao(CBMPreferencesDAO cbmPreferencesDao) {
		this.cbmPreferencesDao = cbmPreferencesDao;
	}
	/**
	 * @return Returns the migrationDao.
	 */
	public MigrationDAO getMigrationDao() {
		return migrationDao;
	}
	/**
	 * @param migrationDao The migrationDao to set.
	 */
	public void setMigrationDao(MigrationDAO migrationDao) {
		this.migrationDao = migrationDao;
	}
}
