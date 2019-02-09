/*
 * Created on 11/08/2007
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
public class PDBPreferencesRollbackDAOSpringJdbcImplTest extends
		AbstractDependencyInjectionSpringContextTests {

	public PDBPreferencesRollbackDAOSpringJdbcImpl pdbPreferencesDAOSpringJdbcImpl;
    
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
		System.out.println("pdbPreferencesDAOSpringJdbcImpl before="+pdbPreferencesDAOSpringJdbcImpl);
		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		System.out.println("pdbPreferencesDAOSpringJdbcImpl after="+pdbPreferencesDAOSpringJdbcImpl);
	}

	public void testGetBackedUpUserAccountPreferencesByCIDN() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.getBackedUpUserAccountPreferencesByCIDN("9501810759");
		} catch (PDBPreferencesRollbackDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

	public void testRestoreUserPreferencesToPDB() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.restoreUserPreferencesToPDB(new ArrayList());
		} catch (PDBPreferencesRollbackDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

	public void testRemoveBackedUpUserAccountPreference() {
		try {
			pdbPreferencesDAOSpringJdbcImpl.removeBackedUpUserAccountPreference("9501810759");
		} catch (PDBPreferencesRollbackDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

}
