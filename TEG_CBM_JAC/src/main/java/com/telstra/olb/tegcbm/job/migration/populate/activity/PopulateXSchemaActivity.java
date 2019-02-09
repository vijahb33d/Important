package com.telstra.olb.tegcbm.job.migration.populate.activity;

import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PopulateXSchemaActivity extends DefaultActivity {
    private MigrationDAO migrationDao;
    private PopulateDao populateDao;
    
    /**
     * Activity that populates the initial set of data to be migrated.
     * 
     * @param input
     *            data iterator provides all the categories that needs to be
     *            migrated.
     * @throws ActivityException
     *             is thrown if there is any exception while accessing the data
     *             or while writing the data set into the migration data store.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    protected void doExecute(Object input) throws ActivityException {
        MigrationType migrationType = (MigrationType) input;
        if (log.isDebugEnabled()) { log.debug("getting the list of available <" + migrationType + "> customers to migrate."); }
        List companyList = null;
        try {
            companyList = populateDao.getCompanies(migrationType);
        } catch (PopulateDataException e) {
            throw new ActivityException("unable to retrieve companies for migration type: " + migrationType, e);
        }
        
        try {
            int added = migrationDao.insertCompanyMigration(companyList);
            log.info(migrationType + ", new companies to migrate: " + added);
        } catch (MigrationDAOException e) {
            throw new ActivityException("unable to populate company list for migration.", e);
        }
        
    }

    /**
     * @return Returns the populateDao.
     */
    public PopulateDao getPopulateDao() {
        return populateDao;
    }
    /**
     * @param populateDao The populateDao to set.
     */
    public void setPopulateDao(PopulateDao populateDao) {
        this.populateDao = populateDao;
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
