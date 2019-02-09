/*
 * Created on 7/08/2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.dao;

import junit.framework.TestCase;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.MigrationTestCaseConstants;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.helper.PropertiesSetterHelper;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationDAOHibernateImplSpringTest extends
		AbstractDependencyInjectionSpringContextTests {

    public MigrationDAO migrationDao;
    
	protected String[] getConfigLocations() {
		return new String[] { MigrationTestCaseConstants.APP_CONTEXT_PARENT, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION_CORE, 
				MigrationTestCaseConstants.APP_CONTEXT_POPULATE };
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#onSetUp()
	 */
	protected void onSetUp() throws Exception {
		super.onSetUp();
		System.out.println("migrationDao before="+migrationDao);
		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		System.out.println("migrationDao after="+migrationDao);
	}
	
	public void testFindCompanies() {
		try {
			migrationDao.findCompanies(ActivityStatus.STATUS_PENDING.getName());
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}
	
	public void testFindCompaniesToMigrate() {
		try {
			migrationDao.findCompaniesToMigrate();
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}
	
	public void testFindCompany() {
		try {
			migrationDao.findCompany("");
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}
	
	public void testFindMigrationActivity() {
		try {
			migrationDao.findMigrationActivity(ActivityStatus.STATUS_PENDING.getName());
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}
	
	public void testFindMigrationAudit() {
		try {
			migrationDao.findMigrationAudit(ActivityStatus.STATUS_PENDING.getName());
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}
	
	public void testFindMigrationStatus() {
		try {
			migrationDao.findMigrationStatus(ActivityStatus.STATUS_PENDING.getName());
		} catch (MigrationDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

}
