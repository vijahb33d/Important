package com.telstra.olb.tegcbm.job.migration.pab.activity;

import java.rmi.RemoteException;
import java.util.List;

import javax.ejb.EJBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.accenture.services.ejb.facade.SessionFacade;
import com.accenture.services.valueobject.AddressBookVO;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.pab.dao.AddressBookDAOException;
import com.telstra.olb.tegcbm.job.migration.pab.dao.CBMAddressBookDAO;

/**
 * This class provides the functionality for the Personal Address book
 * data migration from OLB data store to CBM data store.
 */
public class PABMigrationActivity extends DefaultActivity {

    private static Log log = LogFactory.getLog(PABMigrationActivity.class);

    private CBMAddressBookDAO cbmAddressBookDAO;

    private SessionFacade olbSessionFacade;
    
    private MigrationDAO migrationDao;

    /**
     * Migrates the address book entry from OLBServices to CBM. It makes a call to SessionFacade EJB to get
     * the list of address book entries from OLB and writes it to the CBM's Corporate Address Book.
     * 
     * @param input company migration object.
     * @throws ActivityException wraps any exception throw as part of exception
     * if the OLB address book cannot be retrieved or the address book entry cannot be added to CBM.
     */
    public void doExecute(Object input) throws ActivityException {
        OLBCompanyMigration companyMigration = (OLBCompanyMigration) input;
        String cidn = companyMigration.getCompanyCode();
        AddressBookVO olbAddressBook = null;
        try {
            if (log.isDebugEnabled()) { log.debug("Calling olb to retrieve contacts for cidn: " + cidn);}
			olbAddressBook = getOlbSessionFacade().getContacts(cidn);
            if (log.isDebugEnabled()) { log.debug("Saving address book entries for cidn: " + cidn);}
	        cbmAddressBookDAO.saveAddressBookEntries(olbAddressBook);
        } catch (EJBException e) {
        	throw new ActivityException("unable to retrieve address book for company: " + cidn, e);
		} catch (RemoteException e) {
			throw new ActivityException("unable to retrieve address book for company: " + cidn, e);
		} catch (AddressBookDAOException e) {
			throw new ActivityException("unable to update address book entries for company: " + cidn, e);
		}
    }
    
   
	/**
	 * @return Returns the cbmAddressBookDAO.
	 */
	public CBMAddressBookDAO getCbmAddressBookDAO() {
		return cbmAddressBookDAO;
	}
	/**
	 * @param cbmAddressBookDAO The cbmAddressBookDAO to set.
	 */
	public void setCbmAddressBookDAO(CBMAddressBookDAO cbmAddressBookDAO) {
		this.cbmAddressBookDAO = cbmAddressBookDAO;
	}
	/**
	 * @return Returns the olbSessionFacade.
	 */
	public SessionFacade getOlbSessionFacade() {
		return olbSessionFacade;
	}
	/**
	 * @param olbSessionFacade The olbSessionFacade to set.
	 */
	public void setOlbSessionFacade(SessionFacade olbSessionFacade) {
		this.olbSessionFacade = olbSessionFacade;
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
