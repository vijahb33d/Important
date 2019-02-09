/*
 * Created on 26/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.activity;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.AsynchronousActivity;
import com.telstra.olb.tegcbm.job.core.IContext;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SalmatAsynchronousActivity extends AsynchronousActivity {
	
	/**
     * post execute processing hook. contains implementation that wait for all 
     * concurrent tasks that were spawned to finish.
     * @param activityContext activity input.
     * @throws ActivityException can be thrown by overridden methods.
     */
    protected void postExecute(IContext activityContext) throws ActivityException {
       
    }

}
