package com.telstra.olb.tegcbm.job.migration.pab.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.accenture.services.valueobject.AddressBookVO;
import com.accenture.services.valueobject.ContactDetailVO;
import com.accenture.services.valueobject.ContactNumberVO;

/**
 * Spring Jdbc implementation of <code>CBMAddressBookDAO</code>.
 * 
 * @author d274681
 */
public class CBMAddressBookDAOSpringJdbcImpl extends JdbcDaoSupport implements CBMAddressBookDAO {
	
    private static Log log = LogFactory.getLog(CBMAddressBookDAOSpringJdbcImpl.class);

    private static final String SELECT_SQL = "SELECT c.ID FROM EDX_BSL_CMF_COMPANY c, EDX_BSL_CMF_COMPANYPROFILE cp "
                                           + "where c.PROFILEID = cp.ID and cp.NAME = ?";

    private static final String INSERT_SQL = "INSERT INTO ADDRESS_BOOK_CORPORATE (CORPID, PHONENUMBER, ALIAS, NAME) VALUES (?,?,?,?)";

    private static final String DELETE_SQL = "DELETE ADDRESS_BOOK_CORPORATE WHERE CORPID IN (SELECT ID FROM EDX_BSL_CMF_COMPANY " +
    		"WHERE PROFILEID IN (SELECT ID FROM EDX_BSL_CMF_COMPANYPROFILE WHERE NAME = ?))";

    /**
     * Saves the address book entries to CBM database.
     * 
     * @param olbAddressBook all the address book entries for company
     * @throws AddressBookDAOException thrown if problem accessing database
     */
    public void saveAddressBookEntries(AddressBookVO olbAddressBook) throws AddressBookDAOException {
        if (olbAddressBook.size() > 0) {
            String companyId = retrieveCompanyId(olbAddressBook);
            for (Iterator i = olbAddressBook.getContacts().iterator(); i.hasNext();) {
                ContactDetailVO contact = (ContactDetailVO) i.next();
                saveEntries(companyId, contact);
            }
        }
    }

    /**
     * Saves the address book entries for a company to the cbm corporate address book datastore.
     * 
     * @param companyId company id
     * @param olbContact Contains address book information for a company
     * @throws AddressBookDAOException thrown if problem accessing database
     */
    private void saveEntries(final String companyId, final ContactDetailVO olbContact) throws AddressBookDAOException {
        try {
            final List e1 = getUniqueContactNumbersList(olbContact);
            getJdbcTemplate().batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {

				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
					String contactNumber = (String) e1.get(index);
					insertStat.setString(1, companyId);
                    insertStat.setString(2, contactNumber);
                    insertStat.setString(3, olbContact.getNickname());
                    insertStat.setString(4, olbContact.getFirstName() + " " + olbContact.getLastName());
			        if (log.isDebugEnabled()) { log.debug("Saving contact number: " + contactNumber + " for company:" + companyId);}
				}

				public int getBatchSize() {
					// TODO Auto-generated method stub
					return e1.size();
				}
			});
        } catch (DataAccessException e) {
            throw new AddressBookDAOException("Problem saving address booking entries for company:" + companyId, e);
        }
    }

    /**
     * Creates a unique list of contact numbers and stores them in a list.
     * 
     * @param olbContact Contains address book information for a company
	 * @return list of contact numbers
	 */
	private List getUniqueContactNumbersList(ContactDetailVO olbContact) {
        
        List contactNumbers = new ArrayList();
        for (Iterator i = olbContact.getContactNumbers().iterator(); i.hasNext();) {
            ContactNumberVO contactNumber = (ContactNumberVO) i.next();
            if (!contactNumbers.contains(contactNumber.getNumber())) {
                contactNumbers.add(contactNumber.getNumber());
            }
        }
        if (log.isDebugEnabled()) { log.debug("Found " + contactNumbers.size() + " unique numbers");}
        
        return contactNumbers;
	}

	/**
     * Retrieves the equivalent cbm datastore company id for the pdb cidn.
     * 
     * @param olbAddressBook Address Book Value object
     * @return Company id
     * @throws AddressBookDAOException Exception thrown if there is any problem retrieving the company id
     */
    private String retrieveCompanyId(AddressBookVO olbAddressBook) throws AddressBookDAOException {
        String cidn = olbAddressBook.getContact(0).getCidn();
        try {
            if (log.isDebugEnabled()) { log.debug("Retrieving address book information for cidn:" + cidn);}
            return (String) getJdbcTemplate().queryForObject(SELECT_SQL, new Object[] { cidn }, String.class);
        } catch (DataAccessException e) {
            throw new AddressBookDAOException("unable to retreieve company id for " + cidn, e);
        }
    }

	/**
     * Removes the corporate address book entries for the company from CBM datastore.
     * 
	 * @param cidn Company CIDN
     * @throws AddressBookDAOException Exception thrown if there is any problem removing address book entires for the company id
	 */
	public void removeAddressBookEntries(final String cidn) throws AddressBookDAOException {
        if (log.isDebugEnabled()) { log.debug("Remove records from corp address book table for cidn:" + cidn);}
    	try {
			getJdbcTemplate().update(DELETE_SQL, new PreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat) throws SQLException {
		            deleteStat.setString(1, cidn);
				}
				
			});
		} catch(DataAccessException sqle) {
            throw new AddressBookDAOException("Unable to delete corp address book entries for cidn:" + cidn, sqle);
		}
	}
}
