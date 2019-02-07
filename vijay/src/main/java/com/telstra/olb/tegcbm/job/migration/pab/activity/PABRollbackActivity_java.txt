package com.telstra.olb.tegcbm.job.migration.pab.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.pab.dao.AddressBookDAOException;
import com.telstra.olb.tegcbm.job.migration.pab.dao.CBMAddressBookDAO;

/**
 * This class provides the functionality for the PAB Roll-back activity.
 * It roll-backs the updates that have been performed to the data store
 * as a result of execution of User notification preferences migration.
 */
public class PABRollbackActivity extends DefaultActivity {

    private static Log log = LogFactory.getLog(PABRollbackActivity.class);

    private CBMAddressBookDAO cbmAddressBookDAO;
    
    /**
      * Method defining the functionality for the PAB roll-back activity.
      * Removes the corporate address book entries for the company from CBM data store.
      * 
      * @param input OLBCompanyMigration information
      * @throws ActivityException is thrown if the preferences could not be rolled back.
      */
    public void doExecute(Object  input) throws ActivityException {
    	OLBCompanyMigration company = (OLBCompanyMigration) input;
        try {
            if (log.isDebugEnabled()) { log.debug("Removing address book entries for cidn: " + company.getCompanyCode());}
            cbmAddressBookDAO.removeAddressBookEntries(company.getCompanyCode());
            if (log.isDebugEnabled()) { log.debug("PAB data successfully removed from CBM for cidn:" + company.getCompanyCode());}
		} catch (AddressBookDAOException e) {
			throw new ActivityException("Problem rolling back preferences from cbm for company:" + company.getCompanyCode(), e);
		}
    }
    
	/**
	 * @param cbmAddressBookDAO The cbmAddressBookDAO to set.
	 */
	public void setCbmAddressBookDAO(
			CBMAddressBookDAO cbmAddressBookDAO) {
		this.cbmAddressBookDAO = cbmAddressBookDAO;
	}
}
