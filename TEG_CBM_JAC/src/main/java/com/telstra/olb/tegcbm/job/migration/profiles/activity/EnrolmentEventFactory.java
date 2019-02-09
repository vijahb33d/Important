package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import java.util.Collection;

import com.telstra.olb.tegcbm.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEvent;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification;
import com.telstra.olb.tegcbm.common.AccountRelationType;
import com.telstra.olb.tegcbm.common.AccountType;
import com.telstra.olb.tegcbm.common.EnrolmentType;

/**
 * Factory class to generate EnrolmentEventNotification object and EnrolmentEvent objects. 
 */
public interface EnrolmentEventFactory {
    
    /**
     * Create an enrolment event object for the given company, account, account type and account relation type.
     * 
     * @param enrolmentType
     *            enrolment type.
     * @param companyId
     *            company
     * @param accountNumber
     *            account number
     * @param accountType
     *            account type
     * @param accountRelationType
     *            account relation type
     * @return enrolment event object.
     * @throws EnrolmentEventException
     *             is thrown if the enrolment event object cannot be created.
     */
    EnrolmentEvent createEvent(EnrolmentType enrolmentType, String companyId, String accountNumber, AccountType accountType,
            AccountRelationType accountRelationType) throws EnrolmentEventException;

    /**
     * Creates an new Enrolment Event notification object.
     * 
     * @param enrolmentType enrolment type.
     * @param companyCode company
     * @param accounts list of accounts.
     * @return Enrolment Event notification object
     * @throws EnrolmentEventException
     *             is thrown if there is any exception while creating enrolment
     *             events.
     */
    EnrolmentEventNotification createEnrolmentEventNotification(EnrolmentType enrolmentType, String companyCode, Collection accounts)
        throws EnrolmentEventException;
}
