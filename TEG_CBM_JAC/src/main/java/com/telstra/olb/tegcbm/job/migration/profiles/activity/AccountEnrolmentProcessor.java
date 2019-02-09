package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import com.telstra.olb.tegcbm.accountenrolment.exception.EnrolmentEventException;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification;

/**
 * Interface for notifying an application external to BFM that an enrolment event has occurred.
 * 
 */
public interface AccountEnrolmentProcessor {
	/**
	 * Process account enrolments/de-enrolments.
	 * @param notification enrolment event notification
	 * @throws EnrolmentEventException generic exception is thrown.
	 */
	void process(EnrolmentEventNotification notification) throws EnrolmentEventException;
	/**
	 * returns the factory to create enrolment event objects.
	 *
	 * @return factory class.
	 */
	EnrolmentEventFactory getFactory();
}
