package com.telstra.olb.tegcbm.job.migration.enrol.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcba.accountenrolment.delegate.CBATelstraAcctEnrolmentDelegate;
import com.telstra.olb.tegcba.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcba.accountenrolment.model.EnrolmentEventNotification;

/**
 * MigrationAccountEnrolmentProcessor enrols a collection of accounts for a company by invoking a EJB call to
 * CBMAccountEnrolmentEJB. The CBMAccountErolmentEJB is invoked through a ServiceLocator pattern configured using
 * <code>tegcbmdelegate.properties</code> file. MigrationEnrolmentEventProcessor also has an instance of EnrolmentEventFactory
 * to create EnrolmentEventNotification object to enrol accounts.
 *  
 */
public class CBAEnrolmentProcessor implements AccountEnrolmentProcessor {
    private static Log log = LogFactory.getLog(CBAEnrolmentProcessor.class);

    // Delegate to CBMTelstraAcctEnrolment EJB
    private CBATelstraAcctEnrolmentDelegate accountEnrolmentDelegate;
    
    private EnrolmentEventFactory factory = CBAEnrolmentEventFactory.getInstance();

    /**
     * @return Returns the accountEnrolmentDelegate.
     */
    public CBATelstraAcctEnrolmentDelegate getAccountEnrolmentDelegate() {
        return accountEnrolmentDelegate;
    }
    /**
     * @param accountEnrolmentDelegate The accountEnrolmentDelegate to set.
     */
    public void setAccountEnrolmentDelegate(CBATelstraAcctEnrolmentDelegate accountEnrolmentDelegate) {
        this.accountEnrolmentDelegate = accountEnrolmentDelegate;
    }
    /**
     * returns the migration enrolment event factory instance.
     * @return factory.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.activity.AccountEnrolmentProcessor#getFactory(com.telstra.olb.tegcbm.common.EnrolmentType)
     */
    public EnrolmentEventFactory getFactory() {
        return factory;
    }
    
    /**
     * @param factory The factory to set.
     */
    public void setFactory(EnrolmentEventFactory factory) {
        this.factory = factory;
    }
    /**
     * Delegates it to account enrolment delegate to process enrolment event notification.
     * @param enrolmentEventNotification enrolment event notification
     * @throws EnrolmentEventException is thrown by the underlying delegate.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.activity.AccountEnrolmentProcessor#process(com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification)
     */
    public void process(EnrolmentEventNotification enrolmentEventNotification) throws EnrolmentEventException {
        if (log.isDebugEnabled()) { log.debug("enrolling accounts");}
        getAccountEnrolmentDelegate().processAcctEnrolmentNotification(enrolmentEventNotification);
    }
}
