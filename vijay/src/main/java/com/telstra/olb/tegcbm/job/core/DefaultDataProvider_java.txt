/*
 * Created on 7/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class DefaultDataProvider implements IDataProvider {
	
	protected transient Log log = LogFactory.getLog(getClass());
	/**
	 * .
	 * @param context .
	 * @return data collection.
	 */
    public final Collection getData(IContext context) {
        if (context == null) {
            return loadData(null);
        }
        Collection data = null;
        Object dataItem = context.getInput();
        if ( dataItem != null) {
        	data = new ArrayList();
        	data.add(dataItem);
        } else {
            data = loadData(context);
        }
        return data != null ? data : new ArrayList();
    }
    
    private Collection loadData(IContext context) {
    	Collection data = null;
		if (context != null) {
			data = processContext(context);
		}
		return data != null ? data : doGetData();
    }
    
    protected abstract Collection processContext(IContext context);
    
    protected abstract Collection doGetData();
}
