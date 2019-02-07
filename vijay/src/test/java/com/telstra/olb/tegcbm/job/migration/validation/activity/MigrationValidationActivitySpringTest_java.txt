/*
 * Created on 7/08/2007
 * 
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.validation.activity;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.migration.MigrationTestCaseConstants;
import com.telstra.olb.tegcbm.job.migration.helper.PropertiesSetterHelper;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationValidationActivitySpringTest extends
		AbstractDependencyInjectionSpringContextTests {
	
	private MigrationValidationActivity activity;

	
	protected String[] getConfigLocations() {
		return new String[] { MigrationTestCaseConstants.APP_CONTEXT_PARENT, 
				MigrationTestCaseConstants.APP_CONTEXT_MIGRATION_CORE, 
				MigrationTestCaseConstants.APP_CONTEXT_VALIDATE };
	}
	
	/* (non-Javadoc)
	 * @see org.springframework.test.AbstractDependencyInjectionSpringContextTests#onSetUp()
	 */
	protected void onSetUp() throws Exception {
		super.onSetUp();

		String propertiesFile = "beanMappingNames";
		PropertiesSetterHelper.setAllPropertiesFromSpringContext(this, applicationContext, propertiesFile);
		
		activity = (MigrationValidationActivity) applicationContext.getBean("cbmjac.migrate.validate.activity");
		
	}
	
	public void testCompanyValidation() {
		OLBCompanyMigration o = new OLBCompanyMigration();
		o.setCompanyCode("2844652687");
		try {
			activity.preExecute(null);
			activity.doExecute(o);
		} catch (ActivityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
