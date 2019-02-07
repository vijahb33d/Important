/*
 * Created on 26/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.activity;

import javax.jms.TextMessage;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.salmat.jms.SalmatMessageReceiver;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SalmatClientActivityDelegate extends DefaultActivity {
	
	private SalmatMessageReceiver jmsReceiver;
	
	protected void doExecute(Object input) throws ActivityException {
		TextMessage message = (TextMessage) input;
		jmsReceiver.processMessage(message);
	}

	/**
	 * @return Returns the jmsReceiver.
	 */
	public SalmatMessageReceiver getJmsReceiver() {
		return jmsReceiver;
	}
	/**
	 * @param jmsReceiver The jmsReceiver to set.
	 */
	public void setJmsReceiver(SalmatMessageReceiver jmsReceiver) {
		this.jmsReceiver = jmsReceiver;
	}
}
