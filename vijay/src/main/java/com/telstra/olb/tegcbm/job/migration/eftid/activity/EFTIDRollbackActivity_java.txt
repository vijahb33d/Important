/*
 * EFTIDRollbackActivity.java
 * Created on 20/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.eftid.activity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAO;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * This class provides the functionality for the EFTID Roll-back activity.
 * It roll-backs the updates that have been performed to the data store
 * as a result of execution of EFTID Migration activity.
 */
public class EFTIDRollbackActivity extends DefaultActivity {
	
	private static Log log = LogFactory.getLog(EFTIDRollbackActivity.class);

    
    private EftIdDAO eftIdRollbackDao;
    
    private String macroSegment = "SME";
    
    /**
     * @return Returns the macroSegment.
     */
    public String getMacroSegment() {
        return macroSegment;
    }
    /**
     * @param macroSegment The macroSegment to set.
     */
    public void setMacroSegment(String macroSegment) {
        this.macroSegment = macroSegment;
    }
    
    /**
     * @param eftIdRollbackDao The eftIdRollbackDao to set.
     */
    public void setEftIdRollbackDao(EftIdDAO eftIdRollbackDao) {
        this.eftIdRollbackDao = eftIdRollbackDao;
    }

    /**
     * Updates the eft id for a company to corporate application.
     * 
     * @param input olb company migration object
     * @throws ActivityException is thrown.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    public void doExecute(Object input) throws ActivityException {
		OLBCompanyMigration company = (OLBCompanyMigration) input;
		if (company.getSegment().equals(getMacroSegment())) {
		    try {
                if (log.isDebugEnabled()) { log.debug("Updating application id for eftid rollback for company code:" + company.getCompanyCode());}
	            eftIdRollbackDao.updateApplicationId(company.getCompanyCode());
	        } catch (EftIdDAOException e) {
	            throw new ActivityException("unable to rollback eftid for company: " + company.getCompanyCode(), e);
	        }
		}
    }
}
