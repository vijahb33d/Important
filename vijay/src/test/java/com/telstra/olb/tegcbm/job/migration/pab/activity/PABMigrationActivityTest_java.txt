/*
 * Created on 8/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.pab.activity;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;

import junit.framework.TestCase;

import org.easymock.MockControl;

import com.accenture.services.ejb.facade.SessionFacade;
import com.accenture.services.valueobject.AddressBookVO;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.pab.dao.CBMAddressBookDAO;
import com.telstra.olb.tegcbm.util.EasyMockAwareTestCase;
import com.telstra.olb.tegcbm.util.Recorder;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PABMigrationActivityTest extends EasyMockAwareTestCase {

    private SessionFacade olbSessionFacade;

    private CBMAddressBookDAO cbmAddressBookDAO;
   
    private MigrationDAO migrationDao;

    private OLBCompanyMigration olbCompanyMigration;
	
	private PABMigrationActivity pabMigrationActivity;
	
	protected void setUp() throws Exception {
    	super.setUp();
    	pabMigrationActivity = new PABMigrationActivity();
    	pabMigrationActivity.setName("test.pab.migration");
    }

	private void setupForTests() {
        setMigrationDaoMock();
        setSessionFacadeMock();
        setOLBCompanyMigration();
        setCBMAddressBookDAOMock();
	}

	private void setupForExceptionTests() {
        setMigrationDaoMockException();
        setSessionFacadeMockException();
        setOLBCompanyMigration();
	}
	
	/**
	 * 
	 */
	private void setSessionFacadeMockException() {
        addDependency(SessionFacade.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	SessionFacade olbSessionFacade = (SessionFacade) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	olbSessionFacade.getContacts("");
                    control.setThrowable(new EJBException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        olbSessionFacade = (SessionFacade) getDependency(SessionFacade.class);
        pabMigrationActivity.setOlbSessionFacade(olbSessionFacade);
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
                    control.expectAndThrow(migrationDao.findMigrationAudit(""), new MigrationDAOException(""));
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        migrationDao = (MigrationDAO) getDependency(MigrationDAO.class);
        pabMigrationActivity.setMigrationDao(migrationDao);
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
	private void setSessionFacadeMock() {
        addDependency(SessionFacade.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	SessionFacade olbSessionFacade = (SessionFacade) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                	AddressBookVO olbAddressBook = new AddressBookVO("Address Book");
                	control.expectAndReturn(olbSessionFacade.getContacts(""), olbAddressBook);
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        olbSessionFacade = (SessionFacade) getDependency(SessionFacade.class);
        pabMigrationActivity.setOlbSessionFacade(olbSessionFacade);
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
                	OLBMigrationAudit olbMigrationAudit = new OLBMigrationAudit();
                	OLBMigrationActivity olbMigrationActivity = new OLBMigrationActivity();
                	olbMigrationActivity.setActivityName("test.pab.migration");
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
        pabMigrationActivity.setMigrationDao(migrationDao);
	}
	
	private void setCBMAddressBookDAOMock() {
        addDependency(CBMAddressBookDAO.class, new Recorder() {

            public void record(MockControl control, Object mock) {
            	CBMAddressBookDAO cbmAddressBookDAO = (CBMAddressBookDAO) mock;
                control.setDefaultMatcher(MockControl.ALWAYS_MATCHER);
                try {
                    cbmAddressBookDAO.saveAddressBookEntries(new AddressBookVO(""));
                    control.setVoidCallable();
                } catch (Exception e) {
                    TestCase.fail("unexpected exception:" + e.getMessage());
                }
            }
        });
        cbmAddressBookDAO = (CBMAddressBookDAO) getDependency(CBMAddressBookDAO.class);
        pabMigrationActivity.setCbmAddressBookDAO(cbmAddressBookDAO);
	}

	public void testDoExecute() {
		setupForTests();
        activate();
        
        try {
        	pabMigrationActivity.doExecute(olbCompanyMigration);
		} catch (ActivityException e) {
			 fail("test failed: " + e.getMessage());
		}
        
//		verify();
	}
	
	public void testCanProcess() {
		setupForTests();
        activate();
        
        //boolean b = pabMigrationActivity.canProcess(olbCompanyMigration);
        
//		verify();
	}

	public void testDoExecuteException() {
		setupForExceptionTests();
        activate();
        
        try {
        	pabMigrationActivity.doExecute(olbCompanyMigration);
			fail("Test failed: Should have thrown an exception");
		} catch (ActivityException e) {
		}
        
//		verify();
	}
	
	public void testCanProcessException() {
		setupForExceptionTests();
        activate();
        
        //boolean b = pabMigrationActivity.canProcess(olbCompanyMigration);
        
    	//assertFalse(b);
//		verify();
	}

}
