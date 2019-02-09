/*
 * Created on 8/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.eftid.activity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAO;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.util.EasyMockAwareTestCase;
import com.telstra.olb.tegcbm.util.Recorder;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EFTIDMigrationActivityTest extends EasyMockAwareTestCase {

	EftIdDAO eftIdDao;
	
	MigrationDAO migrationDao;
	
	OLBCompanyMigration olbCompanyMigration;
	
	EFTIDMigrationActivity eftIdMigrationActivity;
	
	protected void setUp() throws Exception {
    	super.setUp();
		eftIdMigrationActivity = new EFTIDMigrationActivity();
		eftIdMigrationActivity.setName("test.migration");
    }

	private void setupForTests() {
        setMigrationDaoMock();
        setEftIdDAOMock();
        setOLBCompanyMigration();
	}

	private void setupForExceptionTests() {
        setMigrationDaoMockException();
        setEftIdDAOMockException();
        setOLBCompanyMigration();
	}
	
	/**
	 * 
	 */
	private void setEftIdDAOMockException() {
        addDependency(EftIdDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	EftIdDAO eftIdDao = (EftIdDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                    eftIdDao.updateApplicationId("");
                    control.setThrowable(new EftIdDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        eftIdDao = (EftIdDAO) getDependency(EftIdDAO.class);
        eftIdMigrationActivity.setEftIdDao(eftIdDao);
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
        eftIdMigrationActivity.setMigrationDao(migrationDao);
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
	private void setEftIdDAOMock() {
        addDependency(EftIdDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	EftIdDAO eftIdDao = (EftIdDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                    eftIdDao.updateApplicationId("");
                    control.setVoidCallable();
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        eftIdDao = (EftIdDAO) getDependency(EftIdDAO.class);
        eftIdMigrationActivity.setEftIdDao(eftIdDao);
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
                	olbMigrationActivity.setActivityName("test.migration");
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
        eftIdMigrationActivity.setMigrationDao(migrationDao);
	}
	
	public void testDoExecute() {
		setupForTests();
        activate();
        
        try {
			eftIdMigrationActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			 fail("test failed: " + e.getMessage());
		}
        
//		verify();
	}
	
	public void testDoExecuteException() {
		setupForExceptionTests();
        activate();
        
        try {
			eftIdMigrationActivity.doExecute(olbCompanyMigration);
			fail("Test failed: Should have thrown an exception");
		} catch (ActivityException e) {
		}
        
//		verify();
	}
	
	public void testCanProcess() {
		setupForTests();
        activate();
        
        boolean b = eftIdMigrationActivity.canProcess(olbCompanyMigration);
        
//		verify();
	}
	
	public void testCanProcessException() {
		setupForExceptionTests();
        activate();
        
        boolean b = eftIdMigrationActivity.canProcess(olbCompanyMigration);
        
    	assertFalse(b);
//		verify();
	}

}
