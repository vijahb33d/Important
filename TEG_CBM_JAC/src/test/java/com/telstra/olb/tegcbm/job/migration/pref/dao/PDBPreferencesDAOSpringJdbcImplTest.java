/*
 * Created on 10/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.telstra.olb.tegcbm.job.migration.MigrationTestCaseConstants;
import com.telstra.olb.tegcbm.job.migration.helper.PropertiesSetterHelper;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PDBPreferencesDAOSpringJdbcImplTest extends
		AbstractDependencyInjectionSpringContextTests {

	public PDBPreferencesDAOSpringJdbcImpl pdbPreferencesDAOSpringJdbcImpl;
    
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
		System.out.println("cbmAddressBookDAOSpringJdbcImpl before="+pdbPreferencesDAOSpringJdbcImpl);
		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		System.out.println("cbmAddressBookDAOSpringJdbcImpl after="+pdbPreferencesDAOSpringJdbcImpl);
	}

	public void testBackupUserAccountPreference() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.backupUserAccountPreference("9501810759", new ArrayList());
		} catch (PDBPreferencesDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

	public void testUpdateApplicationId() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.processSPP(new ArrayList());
		} catch (PDBPreferencesDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

	public void testDeleteUserAccountPreferences() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.deleteUserAccountPreferences(new ArrayList());
		} catch (PDBPreferencesDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

}
