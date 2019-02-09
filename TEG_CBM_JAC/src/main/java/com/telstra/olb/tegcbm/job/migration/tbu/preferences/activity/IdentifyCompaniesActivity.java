package com.telstra.olb.tegcbm.job.migration.tbu.preferences.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;

public class IdentifyCompaniesActivity extends DefaultActivity {

	private List macroSegments;
	
	private PopulateDao identifyCompaniesDao;
	
	private MigrationDAO migrationDao;
	
	/**
	 * Method that executes the activity.
	 * The tbunmanaged companies are identified and moved to the OLB_TBUNMANAGED_MIGRATION table.
	 * @Param Object
	 */
	protected void doExecute(Object input) throws ActivityException {
		log.info("getting the list of available tbunmanaged <" + macroSegments + "> customers to migrate."); 
        List companyList = new ArrayList();
        Iterator iter = macroSegments.iterator();
        
        //Retrieving the tbunmanaged companies
        while(iter.hasNext()){
        	List companies = null;
        	MigrationType migrationType = (MigrationType)iter.next();
		    try {
		        companies = identifyCompaniesDao.getTBUnmanagedCompanies(migrationType);
		        log.info(macroSegments + ", new companies to migrate: " + companies.size());
		    } catch (PopulateDataException e) {
		        throw new ActivityException("unable to retrieve companies for migration type: " + migrationType, e);
		    }
		    companyList.addAll(companies);
        }
        
        //Inserting the retrieved companies into the OLB_TBUMANAGED_MIGRATION table
        try {
            int added = migrationDao.insertTBUnmanagedCompanyMigration(companyList);
        } catch (MigrationDAOException e) {
            throw new ActivityException("unable to populate company list for migration.", e);
        }
	}
    
	/**
	 * @return Returns the identifyCompaniesDao.
	 */
	public PopulateDao getIdentifyCompaniesDao() {
		return identifyCompaniesDao;
	}
	/**
	 * @param identifyCompaniesDao The identifyCompaniesDao to set.
	 */
	public void setIdentifyCompaniesDao(PopulateDao identifyCompaniesDao) {
		this.identifyCompaniesDao = identifyCompaniesDao;
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
	/**
	 * @param macroSegments The macroSegments to set.
	 */
	public void setMacroSegments(List macroSegments) {
		this.macroSegments = macroSegments;
	}
}
