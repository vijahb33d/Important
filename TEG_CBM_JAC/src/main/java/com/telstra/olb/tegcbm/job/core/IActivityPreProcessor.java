/*
 * Created on 1/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.core;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IActivityPreProcessor {

	/**
	 * @param activity2
	 * @param context
	 */
	void process(Activity activity, IContext context);

}
