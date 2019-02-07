/*
 * Created on 8/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.eftid.dao;

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
public class EftIdDAOSpringJdbcImplTest extends
	AbstractDependencyInjectionSpringContextTests {

	public EftIdDAOSpringJdbcImpl eftIdDao;
	
	/* (non-Javadoc)
	 * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#getConfigLocations()
	 */
	protected String[] getConfigLocations() {
		return new String[] { MigrationTestCaseConstants.APP_CONTEXT_PARENT, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION_CORE, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION,
				"testCaseBeanContext.xml" };
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#onSetUp()
	 */
	protected void onSetUp() throws Exception {
		super.onSetUp();
		System.out.println("eftidDao before="+eftIdDao);
		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		System.out.println("eftIdDao after="+eftIdDao);
	}
	
	
	public void testFindMigrationActivity() {
		try {
			eftIdDao.updateApplicationId("9501810759");
		} catch (EftIdDAOException e) {
			TestCase.fail(e.getMessage());
		}
	}

}
