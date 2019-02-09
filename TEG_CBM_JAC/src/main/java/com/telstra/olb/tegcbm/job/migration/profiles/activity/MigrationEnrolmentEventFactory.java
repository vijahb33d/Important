/*
 * EnrolmentEventNotificationFactory.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEvent;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification;
import com.telstra.olb.tegcbm.common.AccountRelationType;
import com.telstra.olb.tegcbm.common.AccountType;
import com.telstra.olb.tegcbm.common.EnrolmentType;
import com.telstra.olb.tegcbm.job.migration.profiles.model.PDBAccountVO;

/**
 * Default implementation of EnrolmentEventFactory for enroling accounts for a company during Migration
 * process. It is a singleton object. Only ENROL and AAL_ENROL enrolment events are supported by this
 * factory. This is because, during Migration, only those two enrolment events are possible. 
 */
public final class MigrationEnrolmentEventFactory implements EnrolmentEventFactory {
    private static Log log = LogFactory.getLog(MigrationEnrolmentEventFactory.class);

    private static MigrationEnrolmentEventFactory instance = null;

    /**
     * returns a singleton instance of the factory to create enrolment event
     * objects.
     * 
     * @return factory instance.
     */
    public static MigrationEnrolmentEventFactory getInstance() {
        if (instance == null) {
            instance = new MigrationEnrolmentEventFactory();
        }
        return instance;
    }

    /**
     * 
     *  
     */
    private MigrationEnrolmentEventFactory() {

    }

    /**
     * Creates enrolment event to add to an EnrolmentEventNotification object.
     * 
     * @param enrolmentType
     *            enrolmentType
     * @param companyId
     *            Company Id
     * @param accountNumber
     *            Account number
     * @param accountType
     *            Account type
     * @param accountRelationType
     *            Account Relation type
     * @return EnrolmentEvent object;
     * @throws EnrolmentEventException
     *             is thrown if the event cannot be created.
     */
    public EnrolmentEvent createEvent(EnrolmentType enrolmentType, String companyId, String accountNumber, AccountType accountType,
            AccountRelationType accountRelationType) throws EnrolmentEventException {
        EnrolmentEvent event = null;
        if (accountType != null && accountRelationType != null) {
            event = createEnrolmentEvent(companyId, accountNumber, enrolmentType, accountType, accountRelationType);
            if (log.isDebugEnabled()) {
                log.debug("event: " + enrolmentType + " created for account: " + accountNumber + ", in company: " + companyId);
            }
        }
        if (event == null) {
            throw new EnrolmentEventException("unable to create an event for " + companyId + " for account: " + accountNumber);
        }
        return event;
    }

    /**
     * Create an account enrolment and de-enrolment event.
     * 
     * @param companyId
     *            Company Id
     * @param accountNumber
     *            Account Number
     * @param enrolmentType
     *            Enrolment Type
     * @param accountType
     *            Account Relation Type
     * @param accountRelationType
     *            Account Relation Type
     * @return event Enrolment event for CBM
     */
    private EnrolmentEvent createEnrolmentEvent(String companyId, String accountNumber, EnrolmentType enrolmentType, AccountType accountType,
            AccountRelationType accountRelationType) {
        EnrolmentEvent event = new EnrolmentEvent();
        event.setAccountId(accountNumber);
        event.setCompanyId(companyId);
        event.setEnrolmentType(enrolmentType);
        event.setAccountType(accountType);
        event.setAccountRelationType(accountRelationType);
        return event;
    }

    /**
     * creates an new Enrolment Event notification object.
     * 
     * @param enrolmentType
     *            enrolment type.
     * @param companyCode
     *            company
     * @param accounts
     *            list of accounts.
     * @return Enrolment Event notification object
     * @throws EnrolmentEventException
     *             is thrown if there is any exception while creating enrolment
     *             events.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.activity.EnrolmentEventFactory#createEnrolmentEventNotification(java.lang.String,
     *      java.util.List)
     */
    public EnrolmentEventNotification createEnrolmentEventNotification(EnrolmentType enrolmentType, String companyCode, Collection accounts)
        throws EnrolmentEventException {
        EnrolmentEventNotification notification = new EnrolmentEventNotification();
        Iterator iterator = accounts.iterator();
        while (iterator.hasNext()) {
            PDBAccountVO accountVO = (PDBAccountVO) iterator.next();
            EnrolmentEvent event = createEvent(getEnrolmentType(enrolmentType, accountVO.getAttributes()), 
            		companyCode, accountVO.getAccountNumber(), getAccountType(accountVO.getAttributes()), 
					getAccountRelationType(accountVO.getAttributes()));
            notification.addEnrolmentEvent(event);
        }
        if (log.isDebugEnabled()) { log.debug("created " + accounts.size() + " enrolments for: " + companyCode); }
        return notification;
    }

    /**
     * gets the enrolment type for the account. If the enrolment type is null, then
     * the account enrolment type is derived from account VO object.
     * @param enrolmentType enrolment type (may be null)
     * @param attributes map of account attributes
     * @return enrolmentType.
     */
    private EnrolmentType getEnrolmentType(EnrolmentType enrolmentType, Map attributes) {
    	if (isNativeAccount(attributes)) {
    		return EnrolmentType.ACCOUNT_ENROLMENT;
    	} else {
            return EnrolmentType.AAL_ENROLMENT;
    	}
    }

    /**
     * gets the account relation type for the account.
     *
     * @param attributes map of account attributes
     * @return account relation type.
     */
    private AccountRelationType getAccountRelationType(Map attributes) {
    	if (isNativeAccount(attributes)) {
            return AccountRelationType.ACCOUNT_NATIVE;
    	} else {
            return AccountRelationType.ACCOUNT_AAL;
    	}
    }

    /**
     * Checks if the account is native.
     * 
     * @param attributes map of account attributes
     * @return true/false
     */
    private boolean isNativeAccount(Map attributes) {
    	String coreAccount = (String)attributes.get("core_account");
    	if (coreAccount.trim().equals("y")) {
            return true;
    	}
        return false;
    }
    /**
     * gets the account type for the account number.
     * 
     * @param attributes map of account attributes
     * @return AccountType
     */
    private AccountType getAccountType(Map attributes) {
        return AccountType.getType((String)attributes.get("type"));
    }

}
