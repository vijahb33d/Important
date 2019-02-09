/*
 * Created on 8/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.pref.activity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesDAOException;
import com.telstra.olb.tegcbm.util.EasyMockAwareTestCase;
import com.telstra.olb.tegcbm.util.Recorder;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PreferencesMigrationActivityTest extends EasyMockAwareTestCase {

    private PDBPreferencesDAO preferencesDao;
    
    private CBMPreferencesDAO cbmPreferencesDao;
    
    private MigrationDAO migrationDao;

    private OLBCompanyMigration olbCompanyMigration;
	
	private PreferencesMigrationActivity preferencesMigrationActivity;
	
	protected void setUp() throws Exception {
    	super.setUp();
    	preferencesMigrationActivity = new PreferencesMigrationActivity();
    	preferencesMigrationActivity.setName("test.pref.migration");
    }

	private void setupForTests() {
        setMigrationDaoMock();
        setPreferencesDaoMock();
        setOLBCompanyMigration();
	}

	private void setupForExceptionTests() {
        setMigrationDaoMockException();
        setPreferencesDaoMockException();
        setOLBCompanyMigration();
	}
	
	/**
	 * 
	 */
	private void setPreferencesDaoMockException() {
        addDependency(PDBPreferencesDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBPreferencesDAO eftIdDao = (PDBPreferencesDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	preferencesDao.processSPP(new ArrayList());
                    control.setThrowable(new PDBPreferencesDAOException(""));
                    control.expectAndThrow(preferencesDao.getUserAccountPreferencesByCIDN(""), new PDBPreferencesDAOException(""));
                    preferencesDao.backupUserAccountPreference("", new ArrayList());
                    control.setThrowable(new PDBPreferencesDAOException(""));
                    preferencesDao.deleteUserAccountPreferences(new ArrayList());
                    control.setThrowable(new PDBPreferencesDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        preferencesDao = (PDBPreferencesDAO) getDependency(PDBPreferencesDAO.class);
        preferencesMigrationActivity.setPreferencesDao(preferencesDao);
	}

	/**
	 * 
	 */
	private void setMigrationDaoMockException() {
        addDependency(MigrationDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	MigrationDAO migrationDao = (MigrationDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndThrow(migrationDao.findCompany(""), new MigrationDAOException(""));
                    control.expectAndThrow(migrationDao.findMigrationAudit(""), new MigrationDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        preferencesMigrationActivity.setMigrationDao(migrationDao);
	}

	/**
	 * 
	 */
	private void setOLBCompanyMigration() {
		olbCompanyMigration = new OLBCompanyMigration();
		olbCompanyMigration.setCompanyCode("CompanyCode");
		olbCompanyMigration.setSegment("SME");
	}

	/**
	 * 
	 */
	private void setPreferencesDaoMock() {
        addDependency(PDBPreferencesDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBPreferencesDAO eftIdDao = (PDBPreferencesDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	preferencesDao.processSPP(new ArrayList());
                    control.setVoidCallable();
                    control.expectAndReturn(preferencesDao.getUserAccountPreferencesByCIDN(""), new ArrayList());
                    preferencesDao.backupUserAccountPreference("", new ArrayList());
                    control.setVoidCallable();
                    preferencesDao.deleteUserAccountPreferences(new ArrayList());
                    control.setVoidCallable();
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        preferencesDao = (PDBPreferencesDAO) getDependency(PDBPreferencesDAO.class);
        preferencesMigrationActivity.setPreferencesDao(preferencesDao);
    }

	/**
	 * 
	 */
	private void setMigrationDaoMock() {
        addDependency(MigrationDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	MigrationDAO migrationDao = (MigrationDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(migrationDao.findCompany(""), olbCompanyMigration);
                	OLBMigrationAudit olbMigrationAudit = new OLBMigrationAudit();
                	OLBMigrationActivity olbMigrationActivity = new OLBMigrationActivity();
                	olbMigrationActivity.setActivityName("test.pref.migration");
                	olbMigrationAudit.setActivity(olbMigrationActivity);
                	OLBMigrationStatus olbMigrationStatus = new OLBMigrationStatus();
                	olbMigrationStatus.setStatus(ActivityStatus.STATUS_COMPLETE.getName());
                	olbMigrationAudit.setActivityStatus(olbMigrationStatus);
                	List list = new ArrayList();
                	list.add(olbMigrationAudit);
                    control.expectAndReturn(migrationDao.findMigrationAudit(""), list);
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        preferencesMigrationActivity.setMigrationDao(migrationDao);
	}
	
	public void testDoExecute() {
		setupForTests();
        activate();
        
        try {
        	preferencesMigrationActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			 fail("test failed: " + e.getMessage());
		}
        
//		verify();
	}
	
	public void testCanProcess() {
		setupForTests();
        activate();
        
        //boolean b = preferencesMigrationActivity.canProcess(olbCompanyMigration);
        
//		verify();
	}

	public void testDoExecuteException() {
		setupForExceptionTests();
        activate();
        
        try {
        	preferencesMigrationActivity.doExecute(olbCompanyMigration);
			fail("Test failed: Should have thrown an exception");
		} catch (ActivityException e) {
		}
        
//		verify();
	}
	
	public void testCanProcessException() {
		setupForExceptionTests();
        activate();
        
       // boolean b = preferencesMigrationActivity.canProcess(olbCompanyMigration);
        
    	//assertFalse(b);
//		verify();
	}

}
