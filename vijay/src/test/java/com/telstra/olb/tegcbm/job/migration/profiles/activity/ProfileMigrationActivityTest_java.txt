/*
 * Created on 9/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.MockControl;

import com.telstra.olb.tegcbm.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO;
import com.telstra.olb.tegcbm.util.EasyMockAwareTestCase;
import com.telstra.olb.tegcbm.util.Recorder;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ProfileMigrationActivityTest extends EasyMockAwareTestCase {
	
    private MigrationDAO migrationDao;
    
    private PDBProfilesDAO profilesPdbDao;

    private AccountEnrolmentProcessor enrolmentprocessor;
    
    private EnrolmentEventFactory enrolmentEventFactory;
    
    private OLBCompanyMigration olbCompanyMigration;
    
    private ProfileMigrationActivity profileMigrationActivity;
	
	protected void setUp() throws Exception {
    	super.setUp();
    	profileMigrationActivity = new ProfileMigrationActivity();
    	profileMigrationActivity.setName("test.profile.migration");
    }

	private void setupForTests() {
        setMigrationDaoMock();
        setPDBProfilesDAOMock();
        setEnrolmentEventFactory();
        setAccountEnrolmentProcessor();
        setOLBCompanyMigration();
	}

	private void setupForExceptionTests() {
        setMigrationDaoMockException();
        setOLBCompanyMigration();
	}

	private void setEnrolmentEventFactory() {
        addDependency(EnrolmentEventFactory.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	EnrolmentEventFactory enrolmentEventFactory = (EnrolmentEventFactory) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(enrolmentEventFactory.
                			createEnrolmentEventNotification(null, null, null), new EnrolmentEventNotification());
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        enrolmentEventFactory = (EnrolmentEventFactory) getDependency(EnrolmentEventFactory.class);
	}

	private void setEnrolmentEventFactoryException() {
        addDependency(EnrolmentEventFactory.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	EnrolmentEventFactory enrolmentEventFactory = (EnrolmentEventFactory) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndThrow(enrolmentEventFactory.
                			createEnrolmentEventNotification(null, null, null), new EnrolmentEventException());
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        enrolmentEventFactory = (EnrolmentEventFactory) getDependency(EnrolmentEventFactory.class);
	}

	private void setPDBProfilesDAOMock() {
        addDependency(PDBProfilesDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBProfilesDAO profilesPdbDao = (PDBProfilesDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	Collection coll = new ArrayList();
                	coll.add("");
                	control.expectAndReturn(profilesPdbDao.getAccountsForCompany(""), coll);
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        profilesPdbDao = (PDBProfilesDAO) getDependency(PDBProfilesDAO.class);
        profileMigrationActivity.setProfilesPdbDao(profilesPdbDao);
	}

	private void setPDBProfilesDAOMockEmptyList() {
        addDependency(PDBProfilesDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	PDBProfilesDAO profilesPdbDao = (PDBProfilesDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(profilesPdbDao.getAccountsForCompany(""), new ArrayList());
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        profilesPdbDao = (PDBProfilesDAO) getDependency(PDBProfilesDAO.class);
        profileMigrationActivity.setProfilesPdbDao(profilesPdbDao);
	}

	private void setAccountEnrolmentProcessor() {
        addDependency(AccountEnrolmentProcessor.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	AccountEnrolmentProcessor enrolmentprocessor = (AccountEnrolmentProcessor) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(enrolmentprocessor.getFactory(), enrolmentEventFactory);
                	enrolmentprocessor.process(null);
                	control.setVoidCallable();
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        enrolmentprocessor = (AccountEnrolmentProcessor) getDependency(AccountEnrolmentProcessor.class);
        profileMigrationActivity.setEnrolmentprocessor(enrolmentprocessor);
	}

	private void setMigrationDaoMock() {
        addDependency(MigrationDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	MigrationDAO migrationDao = (MigrationDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndReturn(migrationDao.findCompany(""), olbCompanyMigration);
                	OLBMigrationAudit olbMigrationAudit = new OLBMigrationAudit();
                	OLBMigrationActivity olbMigrationActivity = new OLBMigrationActivity();
                	olbMigrationActivity.setActivityName("test.profile.migration");
                	olbMigrationAudit.setActivity(olbMigrationActivity);
                	OLBMigrationStatus olbMigrationStatus = new OLBMigrationStatus();
                	olbMigrationStatus.setStatus(ActivityStatus.STATUS_COMPLETE.getName());
                	olbMigrationAudit.setActivityStatus(olbMigrationStatus);
                	List list = new ArrayList();
                	list.add(olbMigrationAudit);
                    control.expectAndReturn(migrationDao.findMigrationAudit(""), list);
                	control.expectAndReturn(migrationDao.findCompaniesToMigrate(), list);
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        //profileMigrationActivity.setMigrationDao(migrationDao);
	}

	private void setMigrationDaoMockException() {
        addDependency(MigrationDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	MigrationDAO migrationDao = (MigrationDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	control.expectAndThrow(migrationDao.findCompany(""), new MigrationDAOException(""));
                    control.expectAndThrow(migrationDao.findMigrationAudit(""), new MigrationDAOException(""));
                    control.expectAndThrow(migrationDao.findCompaniesToMigrate(), new MigrationDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        //profileMigrationActivity.setMigrationDao(migrationDao);
	}
	
	private void setOLBCompanyMigration() {
		olbCompanyMigration = new OLBCompanyMigration();
		olbCompanyMigration.setCompanyCode("CompanyCode");
		olbCompanyMigration.setSegment("SME");
	}

	/*public void testCanProcess() {
		setupForTests();
		activate();
		//boolean b = profileMigrationActivity.canProcess(olbCompanyMigration);
	}
	
	public void testCanProcessException() {
		setupForExceptionTests();
        activate();
        
        //boolean b = profileMigrationActivity.canProcess(olbCompanyMigration);
        
    	assertFalse(b);
//		verify();
	}*/

	public void testDoExecute() {
		setupForTests();
		activate();
		try {
			profileMigrationActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			TestCase.fail("Activity exception:" + e.getMessage());
		}
	}
	
	public void testDoExecuteEmptyList() {
        setMigrationDaoMock();
        setPDBProfilesDAOMockEmptyList();
        setEnrolmentEventFactoryException();
        setAccountEnrolmentProcessor();
        setOLBCompanyMigration();
		activate();
		try {
			profileMigrationActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			TestCase.fail("Activity exception was expected.");
		}
	}
	
	public void testDoExecuteException() {
        setMigrationDaoMock();
        setPDBProfilesDAOMock();
        setEnrolmentEventFactoryException();
        setAccountEnrolmentProcessor();
        setOLBCompanyMigration();
		activate();
		try {
			profileMigrationActivity.doExecute(olbCompanyMigration);
			TestCase.fail("Activity exception was expected.");
		} catch (ActivityException e) {
		}
	}

	/*public void testDoGetData() {
		setupForTests();
		activate();
		Iterator it = profileMigrationActivity.doGetData();
		if (!it.hasNext()) {
			TestCase.fail("No elements returned");
		}
	}

	public void testDoGetDataException() {
		setupForExceptionTests();
		activate();
		Iterator it = profileMigrationActivity.doGetData();
		if (it.hasNext()) {
			TestCase.fail("Elements returned. Should be empty.");
		}
	}*/

}
