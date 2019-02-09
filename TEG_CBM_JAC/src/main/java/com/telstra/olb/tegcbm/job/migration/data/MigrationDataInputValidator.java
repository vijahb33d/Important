/*
 * Created on 1/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.data;

import java.util.List;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.IInputValidator;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationDataInputValidator implements IInputValidator {
	private MigrationDAO migrationDao;
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.IInputValidator#validateInput(java.lang.Object, com.telstra.olb.tegcbm.job.core.Activity)
	 */
	public boolean validateInput(Object input, Activity activity)
			throws ActivityException {
		OLBCompanyMigration current = (OLBCompanyMigration) input;
        try {
            List auditHistory = migrationDao.findMigrationAudit(current.getCompanyCode(),activity.getName(), ActivityStatus.STATUS_COMPLETE.getName());
            return auditHistory.size() <= 0;
        } catch (MigrationDAOException e) {
            return false;
        }
	}

	/**
	 * @return Returns the migrationDao.
	 */
	public MigrationDAO getMigrationDao() {
		return migrationDao;
	}
	/**
	 * @param migrationDao The migrationDao to set.
	 */
	public void setMigrationDao(MigrationDAO migrationDao) {
		this.migrationDao = migrationDao;
	}
}
