/*
 * Created on 13/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.pref.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesRollbackDAO;
import com.telstra.olb.tegcbm.job.migration.pref.dao.PDBPreferencesRollbackDAOException;
import com.telstra.olb.tegcbm.job.migration.pref.model.UserAccountPreference;
import com.telstra.olb.tegcbm.util.EasyMockAwareTestCase;
import com.telstra.olb.tegcbm.util.Recorder;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PreferencesRollbackActivityTest extends EasyMockAwareTestCase {
	
    private PDBPreferencesRollbackDAO preferencesRollbackDao;
    
    private MigrationDAO migrationDao;
    
    private OLBCompanyMigration olbCompanyMigration;
    
    private PreferencesRollbackActivity preferencesRollbackActivity;
	
	protected void setUp() throws Exception {
    	super.setUp();
    	preferencesRollbackActivity = new PreferencesRollbackActivity();
    	preferencesRollbackActivity.setName("test.pref.rollback");
    }

	private void setupForTests() {
        setMigrationDaoMock();
        setPDBPreferencesRollbackDAOMock();
        setOLBCompanyMigration();
	}

	private void setupForExceptionTests() {
        setMigrationDaoMockException();
        setPDBPreferencesRollbackDAOMockException();
        setOLBCompanyMigration();
	}
	
	private void setOLBCompanyMigration() {
		olbCompanyMigration = new OLBCompanyMigration();
		olbCompanyMigration.setCompanyCode("CompanyCode");
		olbCompanyMigration.setSegment("SME");
	}
	
	private void setPDBPreferencesRollbackDAOMock() {
        addDependency(PDBPreferencesRollbackDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBPreferencesRollbackDAO eftIdRollbackDao = (PDBPreferencesRollbackDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	UserAccountPreference userAccountPreferences = new UserAccountPreference("", "", "", "", "");
                	List listPref = new ArrayList();
                	listPref.add(userAccountPreferences);
                	control.expectAndReturn(eftIdRollbackDao.getBackedUpUserAccountPreferencesByCIDN("9501810759"), listPref);
                	eftIdRollbackDao.removeBackedUpUserAccountPreference("");
                    control.setVoidCallable();
                    eftIdRollbackDao.restoreUserPreferencesToPDB(listPref);
                    control.setVoidCallable();
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        preferencesRollbackDao = (PDBPreferencesRollbackDAO) getDependency(PDBPreferencesRollbackDAO.class);
        preferencesRollbackActivity.setPreferencesDao(preferencesRollbackDao);
    }
	
	private void setMigrationDaoMock() {
        addDependency(MigrationDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	MigrationDAO migrationDao = (MigrationDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(migrationDao.findCompany(""), olbCompanyMigration);
                	OLBMigrationActivity olbMigrationActivity = new OLBMigrationActivity();
                	olbMigrationActivity.setActivityName("test.pref.rollback");
                	OLBMigrationStatus olbMigrationStatus = new OLBMigrationStatus();
                	olbMigrationStatus.setStatus(ActivityStatus.STATUS_COMPLETE.getName());
                	List list = new ArrayList();
                	list.add(olbCompanyMigration);
                    control.expectAndReturn(migrationDao.findCompanies(""), list);
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        //preferencesRollbackActivity.setMigrationDao(migrationDao);
	}

	private void setPDBPreferencesRollbackDAOMockException() {
        addDependency(PDBPreferencesRollbackDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBPreferencesRollbackDAO eftIdRollbackDao = (PDBPreferencesRollbackDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	eftIdRollbackDao.getBackedUpUserAccountPreferencesByCIDN("");
                    control.setThrowable(new PDBPreferencesRollbackDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        preferencesRollbackDao = (PDBPreferencesRollbackDAO) getDependency(PDBPreferencesRollbackDAO.class);
        preferencesRollbackActivity.setPreferencesDao(preferencesRollbackDao);
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
                    control.expectAndThrow(migrationDao.findCompanies(""), new MigrationDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        //preferencesRollbackActivity.setMigrationDao(migrationDao);
	}

	public void testDoExecute() {
		setupForTests();
        activate();
        
        try {
        	preferencesRollbackActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			 fail("test failed: " + e.getMessage());
		}
        
//		verify();
	}

	/*public void testDoGetData() {
		setupForTests();
        activate();
        
    	Iterator it = preferencesRollbackActivity.doGetData();
    	if (!it.hasNext()) {
    		fail("Test failed: Iterator size cannot be zero");
    	}
		
//		verify();
	}*/

	public void testDoExecuteException() {
		setupForExceptionTests();
        activate();
        
        try {
        	preferencesRollbackActivity.doExecute(olbCompanyMigration);
			fail("Test failed: Should have thrown an exception");
		} catch (ActivityException e) {
		}
        
//		verify();
	}

	/*public void testDoGetDataException() {
		setupForExceptionTests();
        activate();
        
    	Iterator it = preferencesRollbackActivity.doGetData();
    	if (it.hasNext()) {
    		fail("Test failed: Iterator should not have any elements");
    	}
		
//		verify();
	}*/
}
