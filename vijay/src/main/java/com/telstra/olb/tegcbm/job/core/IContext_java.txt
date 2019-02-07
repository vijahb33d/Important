/*
 * Created on 7/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.Set;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IContext {
	Object getInput();
	
	Object getProperty(String name);
	
	Set getPropertyNames();
}
