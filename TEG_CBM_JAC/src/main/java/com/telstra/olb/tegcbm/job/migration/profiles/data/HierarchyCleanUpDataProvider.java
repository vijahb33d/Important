/*
 * Created on Feb 18, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.populate.dao.OlbDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PdbDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HierarchyCleanUpDataProvider extends DefaultDataProvider {
    private PopulateDao populateDao;
    public static final String INPUT_ARG_CATEGORIES = "companyList";
    
    
    protected Collection processContext(IContext context) {
    	String categories = (String) context.getProperty(INPUT_ARG_CATEGORIES);
    	List categoriesList=null;
		Map companyMap = new HashMap();
		List inputList=null;
		Iterator companyListIterator=null;
		try {
			if (categories != null) {
				inputList = new ArrayList();
				companyMap = populateDao.getCompanyByCidn(categories);
				categoriesList = MigrationHelper.parse(categories, ",");
				Collections.sort(categoriesList);
				Iterator types = categoriesList.iterator();
				while (types.hasNext()) {
					String type = (String) types.next();
					if (!(companyMap == null && companyMap.isEmpty())) {
						if (companyMap.get(type) != null) {
							OLBCompanyMigration company = (OLBCompanyMigration) companyMap.get(type);
							if (company.getActivityStatus() == 1||company.getActivityStatus() == 4) {
								log.debug("Companies are in pending or error status "+ type);
								inputList.add(companyMap.get(type));
							} else {
								log.debug("Companies are in processing or complete status "+ type);
							}
						} else {
							log.debug("No Companies Found for the cidn Provided "+ type);
						}
					}
				}
			}
		} catch(Exception e) {
    		log.error("Exception occured while retrieving CIDNs ",e);
    	}
       
    	return inputList;
		
	}

    protected Collection doGetData() {
		//		Retrieve all the CIDNs from OLB TBUnmanaged migration table
    	log.info("Entering doGetData() of HierarchyCleanUpDataProvider");
    	List companyList = null;
    	try {
    	    companyList = populateDao.getCompanyList(MigrationType.TBU);
    		log.info("Total numbers of CIDNs to be migrated :  "+companyList.size());    		
    	} catch(Exception e) {
    		log.error("Exception occured while retrieving CIDNs ",e);
    	}
    	return companyList != null ? companyList : new ArrayList();
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
