/*
 * EFTIDMigrationActivity.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.eftid.activity;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAO;
import com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * This class provides the functionality for the EFTID Migration activity.
 * 
 */
public class EFTIDMigrationActivity extends DefaultActivity {
    
	private static Log log = LogFactory.getLog(EFTIDMigrationActivity.class);

    private EftIdDAO eftIdDao;
    
    private String macroSegment = "SME";
    
    private MigrationDAO migrationDao;

    /**
     * Returns the macro segment.
     * 
     * @return Returns the macroSegment.
     */
    public String getMacroSegment() {
        return macroSegment;
    }
    /**
     * Sets the macro segment.
     * 
     * @param macroSegment The macroSegment to set.
     */
    public void setMacroSegment(String macroSegment) {
        this.macroSegment = macroSegment;
    }
    /**
     * Sets the EftIdDAO.
     * 
     * @param eftIdDao The eftIdDao.
     */
    public void setEftIdDao(EftIdDAO eftIdDao) {
        this.eftIdDao = eftIdDao;
    }

    /**
     * Updates the eft id for a company to corporate application.
     * 
     * @param input olb company migration object
     * @throws ActivityException exception is thrown if there is problem updating the eft id for company
     */
    public void doExecute(Object input) throws ActivityException {
        OLBCompanyMigration companyMigration = (OLBCompanyMigration) input;
        String companyCode = companyMigration.getCompanyCode();
        if (companyMigration.getSegment().equals(getMacroSegment())) {
            try {
                if (log.isDebugEnabled()) { log.debug("Updating application id for company code:" + companyCode);}
                eftIdDao.updateApplicationId(companyCode);
            } catch (EftIdDAOException e) {
                throw new ActivityException("unable to update eftid for company: " + companyCode, e);
            }
        }
    }
    /**
     * Checks whether the company migration audit has marked this activity completed. If it has completed,
     * then there is no need to re-process this activity.
     * 
     * @param input company migration object.
     * @return if this activity is completed, then 
     */
    protected boolean canProcess(Object input) {
        OLBCompanyMigration current = (OLBCompanyMigration) input;
        try {
            List auditHistory = migrationDao.findMigrationAudit(current.getCompanyCode(), getName(), ActivityStatus.STATUS_COMPLETE.getName());
            return auditHistory.size() <= 0;
        } catch (MigrationDAOException e) {
            return false;
        }
    }
	/**
	 * Returns the MigrationDao.
	 * 
	 * @return Returns the migrationDao.
	 */
	public MigrationDAO getMigrationDao() {
		return migrationDao;
	}
	/**
	 * Set the migration DAO.
	 * 
	 * @param migrationDao The migrationDao to set.
	 */
	public void setMigrationDao(MigrationDAO migrationDao) {
		this.migrationDao = migrationDao;
	}
}
