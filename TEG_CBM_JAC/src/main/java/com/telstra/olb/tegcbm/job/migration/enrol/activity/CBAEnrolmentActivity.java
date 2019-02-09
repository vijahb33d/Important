/*
 * ProfileMigrationActivity.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.enrol.activity;

import java.util.Collection;

import com.telstra.olb.tegcba.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcba.accountenrolment.model.EnrolmentEventNotification;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAOException;

/**
 * <p>Profile Migration Activity performs migration of profiles by invoking the CBMAccountEnrolmentEJB. It retrieves 
 * the list of accounts from the PDB for the given company and creates enrolment events for each account and invokes
 * the EJB to process enrolments. </p>
 * <br/>
 * <p>It also implements the data retrieval methods for profile migration process. The two data retrieval methods are <br/>
 * <li>processInputArgs - gets the property companyList from the command line and processes only those companies in the list
 * that has not been processed or erred.</li>
 * <li>doGetData - retrieves a list of OLBCompanyMigration objects for the database that has not been processed or
 * erred.</li>
 * </p>
 * <br/>
 * <p>If the company is an erred company and the profile migration activity has been completed previously, then 
 * the profile migration activity is not executed for the company.</p>
 */
public class CBAEnrolmentActivity extends DefaultActivity {
    
    private PDBProfilesDAO profilesPdbDao;
    private AccountEnrolmentProcessor enrolmentprocessor;
    
    /**
     * execute migration of profiles.
     * 
     * @param input
     *            activity input
     * @throws ActivityException
     *             is thrown if the profile cannot be migrated.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    public void doExecute(Object input) throws ActivityException {
        OLBCompanyMigration current = (OLBCompanyMigration) input;
        String companyCode = current.getCompanyCode();
        Collection accounts = getAccounts(companyCode);
        if (accounts.isEmpty()) {
            log.info("no accounts found for the company: " + companyCode + ", profile migration skipped...");
            return;
        }
        try {
            EnrolmentEventFactory factory = enrolmentprocessor.getFactory();
            EnrolmentEventNotification notification = factory.createEnrolmentEventNotification(null, companyCode,
                   accounts);
            enrolmentprocessor.process(notification);
        } catch (EnrolmentEventException e) {
            throw new ActivityException("unable to process enrolments for company: " + companyCode, e);
        }
    }
    /**
     * Fetches the list of accounts for a company.
     * 
     * @param companyCode
     *            company code
     * @return collection of accounts to be enrolled.
     * @throws ActivityException
     *             is thrown if the accounts for the company cannot be
     *             retrieved.
     */
    private Collection getAccounts(String companyCode) throws ActivityException {
        try {
            return profilesPdbDao.getAccountsForCompany(companyCode);
        } catch (PDBProfilesDAOException e) {
            throw new ActivityException("unable to get accounts for company: " + companyCode, e);
        }
    }
    /**
     * @return Returns the pdbDao.
     */
    public PDBProfilesDAO getProfilesPdbDao() {
        return profilesPdbDao;
    }
    /**
     * @param pdbDao The pdbDao to set.
     */
    public void setProfilesPdbDao(PDBProfilesDAO pdbDao) {
        this.profilesPdbDao = pdbDao;
    }

    /**
     * @return Returns the enrolment processor.
     */
    public AccountEnrolmentProcessor getEnrolmentprocessor() {
        return enrolmentprocessor;
    }

    /**
     * @param enrolmentprocessor
     *            The enrolment processor to set.
     */
    public void setEnrolmentprocessor(AccountEnrolmentProcessor enrolmentprocessor) {
        this.enrolmentprocessor = enrolmentprocessor;
    }
}
