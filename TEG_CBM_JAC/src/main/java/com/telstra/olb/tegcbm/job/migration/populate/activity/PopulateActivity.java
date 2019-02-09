/*
 * PopulateActivity.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.activity;

import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.populate.dao.OlbDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PdbDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;

/**
 * Populate Activity gets all the own codes for a given Migration Type from OLBServices and get the list of
 * companies for the own codes and populates the companies to migrate. If the company already 
 * exists in the current set of persisted data, then the company is not persisted.
 * 
 */
public class PopulateActivity extends DefaultActivity {
    private OlbDao olbServicesDao;
    private PdbDao pdbDao;
    private MigrationDAO migrationDao;

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
            OwnCodes ownCodes = olbServicesDao.getOwnCodes(migrationType);
            companyList = pdbDao.getCompanies(ownCodes);
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
     * @return Returns the migrationDao.
     */
    public MigrationDAO getMigrationDao() {
        return migrationDao;
    }

    /**
     * @param migrationDao
     *            The migrationDao to set.
     */
    public void setMigrationDao(MigrationDAO migrationDao) {
        this.migrationDao = migrationDao;
    }

    /**
     * @return Returns the olbServicesDao.
     */
    public OlbDao getOlbServicesDao() {
        return olbServicesDao;
    }

    /**
     * @param olbServicesDao
     *            The olbServicesDao to set.
     */
    public void setOlbServicesDao(OlbDao olbServicesDao) {
        this.olbServicesDao = olbServicesDao;
    }

    /**
     * @return Returns the pdbDao.
     */
    public PdbDao getPdbDao() {
        return pdbDao;
    }

    /**
     * @param pdbDao
     *            The pdbDao to set.
     */
    public void setPdbDao(PdbDao pdbDao) {
        this.pdbDao = pdbDao;
    }
}
