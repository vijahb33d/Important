package com.telstra.olb.tegcbm.job.migration.pab.dao;

import com.accenture.services.valueobject.AddressBookVO;

/**
 * DAO Inteface to the CBM Address Book table.
 *
 */
public interface CBMAddressBookDAO {
	
    /**
	 * Saves the address book entries to CBM database.
	 * 
	 * @param olbAddressBook Address book entries for the company
	 * @throws AddressBookDAOException upon exception
	 */
	void saveAddressBookEntries(AddressBookVO olbAddressBook) throws AddressBookDAOException;

	/**
     * Removes the corporate address book entries for the company from CBM datastore.
     * 
	 * @param companyCode Company CIDN
	 * @throws AddressBookDAOException upon exception
	 */
	void removeAddressBookEntries(String companyCode) throws AddressBookDAOException;

}
