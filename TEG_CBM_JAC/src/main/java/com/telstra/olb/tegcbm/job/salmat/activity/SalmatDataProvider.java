/*
 * Created on 26/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.activity;

import java.util.Collection;

import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.salmat.jms.SalmatMessageReceiver;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SalmatDataProvider extends DefaultDataProvider {
	
	private SalmatMessageReceiver jmsReceiver;

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#processContext(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	protected Collection processContext(IContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#doGetData()
	 */
	protected Collection doGetData() {
		// TODO Auto-generated method stub
		return null;
	}

}
