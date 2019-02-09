/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.tbu.preferences.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrateTBUPreferencesDataProvider extends DefaultDataProvider {
	private PopulateDao populateDao;
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#processContext(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	protected Collection processContext(IContext context) {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#doGetData()
	 */
	protected Collection doGetData() {
		List cidns = null;
		//		Retrieve all the CIDNs from OLB TBUnmanaged migration table
    	log.info("Entering doGetData() of MigrateTBUnmanagedPreferencesActivity");
    	try {
    		cidns = populateDao.getTBUnmanagedCIDNs();
    		log.info("Total numbers of CIDNs to be migrated :  "+cidns.size());    		
    	} catch(Exception e) {
    		log.error("Exception occured while retrieving CIDNs ",e);
    	}
    	return cidns != null ? cidns : new ArrayList();
	}

	/**
	 * @return Returns the populateDao.
	 */
	public PopulateDao getPopulateDao() {
		return populateDao;
	}
	/**
	 * @param populateDao The populateDao to set.
	 */
	public void setPopulateDao(PopulateDao populateDao) {
		this.populateDao = populateDao;
	}
}
