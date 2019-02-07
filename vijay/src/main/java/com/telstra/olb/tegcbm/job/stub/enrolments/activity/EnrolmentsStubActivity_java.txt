/*
 * Created on 28/11/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.stub.enrolments.activity;

import java.util.Iterator;

import com.telstra.olb.tegcbm.accountenrolment.delegate.CBMTelstraAcctEnrolmentDelegate;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEvent;
import com.telstra.olb.tegcbm.accountenrolment.model.EnrolmentEventNotification;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.core.IContext;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnrolmentsStubActivity extends DefaultActivity {
	public static final String DISABLE_BUFFER_ENROLMENTS = "disableBufferEnrolments";
	
	private CBMTelstraAcctEnrolmentDelegate delegate;
	
	/**
	 * @param activityContext input to this activity.
	 * @throws ActivityException is thrown.
	 */
	protected void preExecute(IContext activityContext) throws ActivityException {
		boolean disableBufferEnrolments = Boolean.valueOf((String)activityContext.getProperty(DISABLE_BUFFER_ENROLMENTS)).booleanValue();
		if (disableBufferEnrolments) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("reprocessing failed enrolments.");
		}
		try {
			delegate.processBufferEnrolments();
		} catch (Exception e) {
			throw new ActivityException(getName(), e);
		}
	}
	/**
	 * performs process Enrolment on each activityInputItem (EbnrolmentEventNotification object)
	 * got from the datatSource.
	 * @param inputItem EnrolmentEventNotification object derived from activityInput.
	 * @throws ActivityException is thrown.
	 */
	protected void doExecute(Object inputItem) throws ActivityException {
		EnrolmentEventNotification notification = (EnrolmentEventNotification) inputItem;
		try {
			debug(notification);
			delegate.processAcctEnrolmentNotification(notification);
		} catch (Exception e) {
			throw new ActivityException(getName(), e);
		}
	}
	
	/**
	 * @param notification notification object
	 */
	private void debug(EnrolmentEventNotification notification) {
		if (!log.isDebugEnabled()) {
			return;
		}
		for (Iterator events = notification.getAccountEnrolments().iterator(); events.hasNext(); ) {
			EnrolmentEvent event = (EnrolmentEvent) events.next();
			log.debug(event);
		}
	}
	/**
	 * @return Returns the delegate.
	 */
	public CBMTelstraAcctEnrolmentDelegate getDelegate() {
		return delegate;
	}
	/**
	 * @param delegate The delegate to set.
	 */
	public void setDelegate(CBMTelstraAcctEnrolmentDelegate delegate) {
		this.delegate = delegate;
	}
}
