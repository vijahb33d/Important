package com.telstra.olb.tegcbm.job.migration.tbu.preferences.activity;

import java.util.ArrayList;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;

public class RemoveCompaniesActivity extends DefaultActivity {
	
	private static final MigrationType migrationType = MigrationType.SME;
	
	private PopulateDao populateDao;

	/**
	 * This method will take a back-up of the companies to be removed from the account-company map table and then delete them from the table. 
	 * @param input
	 */
	protected void doExecute(Object input) throws ActivityException {
		List accountCompanies = new ArrayList();
		try {
			//Retrieving the list of accounts that belong to the successfully migrated companies.
			accountCompanies = populateDao.getAccountCompanies(migrationType);
		} catch (PopulateDataException e) {
			 throw new ActivityException("unable to retrieve companies from OLB_ACCOUNT_COMPANY_MAP", e);
		}
		
		if(accountCompanies != null){
			try {
				// Backup of account company record in the OLB_ACCOUNT_COMPANY_MAP_BKUP table.
				populateDao.backupAccountCompany(accountCompanies);
				// Deleting the account company records which contains the successfully migrated companies.
				populateDao.deleteAccountCompany(accountCompanies);
			} catch (PopulateDataException e) {
				throw new ActivityException("unable to backup/remove companies from OLB_ACCOUNT_COMPANY_MAP", e);
			}
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
}
