/*
 * Created on 16/07/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.dao;

import java.util.List;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationDAOHibernateImplTest extends TestCase {

    private static final String CONTEXT_FILE = "/com/telstra/olb/tegcbm/job/migration/config/companyMigrationContext.xml";
    
    private MigrationDAO migrationDAO = null;
    
    public MigrationDAOHibernateImplTest() {
        super();
        initTestCase();
    }

    public MigrationDAOHibernateImplTest(String arg0) {
        super(arg0);
        initTestCase();
    }

    private void initTestCase() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] { CONTEXT_FILE });
        this.migrationDAO = (MigrationDAO) context.getBean("cm.migrationDAO");
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    
	public void testFindCompany() {
        List companyCodes = null;
        try {
            companyCodes = migrationDAO.findMigrationAudit("", "cbmjac.migrate.profile", ActivityStatus.STATUS_PENDING.getName());
        } catch (MigrationDAOException e) {
            TestCase.fail(e.getMessage());
        }
        //int i = 1;
        //System.out.println("No. of company codes:" + companyCodes.size());
        //for (Iterator itCompanyCodes = companyCodes.iterator(); itCompanyCodes.hasNext(); i++) {
        //	OLBCompanyMigration company = (OLBCompanyMigration)itCompanyCodes.next();
        //    System.out.println(i + ":Company Code:" + company.getCompanyCode());
        //    System.out.println(i + ":Segment:" + company.getSegment());
        //}
	}

}
