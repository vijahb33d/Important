/*
 * Created on 10/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.pab.dao;

import junit.framework.TestCase;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.accenture.services.valueobject.AddressBookVO;
import com.accenture.services.valueobject.ContactDetailVO;
import com.accenture.services.valueobject.ContactNumberVO;
import com.telstra.olb.tegcbm.job.migration.MigrationTestCaseConstants;
import com.telstra.olb.tegcbm.job.migration.helper.PropertiesSetterHelper;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBMAddressBookDAOSpringJdbcImplTest extends 
	AbstractDependencyInjectionSpringContextTests {

	public CBMAddressBookDAOSpringJdbcImpl cbmAddressBookDAOSpringJdbcImpl;
    
	protected String[] getConfigLocations() {
		return new String[] { MigrationTestCaseConstants.APP_CONTEXT_PARENT, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION_CORE, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION };
	}
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void onSetUp() throws Exception {
		super.onSetUp();
		System.out.println("cbmAddressBookDAOSpringJdbcImpl before="+cbmAddressBookDAOSpringJdbcImpl);
		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		System.out.println("cbmAddressBookDAOSpringJdbcImpl after="+cbmAddressBookDAOSpringJdbcImpl);
	}

	public void testSaveAddressBookEntries() {
		AddressBookVO olbAddressBook = new AddressBookVO();
		ContactDetailVO contactDetailVO = new ContactDetailVO("9501810759");
		contactDetailVO.setCompanyName("9501810759");
		contactDetailVO.setContactNumber(new ContactNumberVO("96325218", "96325218"));
		contactDetailVO.setCountry("Australia");
		contactDetailVO.setEmail("Deepak.Bawa@team.telstra.com");
		contactDetailVO.setFirstName("Deepak");
		contactDetailVO.setLastName("Bawa");
		contactDetailVO.setNickname("Bawa");
		contactDetailVO.setPostcode("3053");
		olbAddressBook.addContact(contactDetailVO);
		try {
			cbmAddressBookDAOSpringJdbcImpl.saveAddressBookEntries(olbAddressBook);
		} catch (AddressBookDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

}
