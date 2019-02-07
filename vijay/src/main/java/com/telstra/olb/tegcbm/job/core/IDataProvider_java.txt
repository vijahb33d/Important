package com.telstra.olb.tegcbm.job.core;

import java.util.Collection;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IDataProvider {
	Collection getData(IContext context);
}