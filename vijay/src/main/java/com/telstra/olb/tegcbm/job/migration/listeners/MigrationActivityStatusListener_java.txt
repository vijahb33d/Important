/*
 * MigrationActivityStatusListener.java
 * Created on 24/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.listeners;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyPreference;

/**
 * Listener that listens on the overall status of migration of a company.
 */
public class MigrationActivityStatusListener extends DefaultActivityStatusListener {

    /**
     * Updates the status of the company migration object.
     * Added conditions for OLBTBUnmanagedCompanyPreference - CR3134
     * 
     * @param activity activity.
     * @param eventParams event params. 
     * @see com.telstra.olb.tegcbm.job.migration.listeners.DefaultActivityStatusListener#doHandleActivityStatus(com.telstra.olb.tegcbm.job.core.Activity, java.lang.Object)
     */
    protected void doHandleActivityStatus(Activity activity, Object eventParams) {
        if (eventParams instanceof OLBCompanyMigration) {
            updateMigrationStatus((OLBCompanyMigration) eventParams, getActivityStatus());
        } else if (eventParams instanceof Object[]) {
            Object[] errorParams = (Object[]) eventParams;
            if(errorParams[0] instanceof OLBCompanyMigration){
            	updateMigrationStatus((OLBCompanyMigration) errorParams[0], getActivityStatus());
            } else if(errorParams[0] instanceof OLBTBUnmanagedCompanyPreference){
            	OLBTBUnmanagedCompanyPreference pref = (OLBTBUnmanagedCompanyPreference) errorParams[0];
            	updateMigrationStatus((OLBCompanyMigration) pref.getCompany(), getActivityStatus());
            }
        } else if (eventParams instanceof OLBTBUnmanagedCompanyPreference){
        	OLBTBUnmanagedCompanyPreference companyPreference = (OLBTBUnmanagedCompanyPreference)eventParams;
        	updateMigrationStatus(companyPreference.getCompany(), getActivityStatus());
        }
        else {
            if (log.isDebugEnabled()) {
                log.debug("status: " + getActivityStatus() + " cannot be handled by " + getClass().getName() + ". Reason: Wrong or null event Params");
            }
        }
    }

    /**
     * updates the status of the company migration object with the given activity status.
     *
     * @param companyMigration company migration object.
     * @param activityStatus  activity status to set.
     */
    protected void updateMigrationStatus(OLBCompanyMigration companyMigration, ActivityStatus activityStatus) {
        companyMigration.setActivityStatus(activityStatus.getValue());
        try {
            if (getMigrationDao() != null) {
                getMigrationDao().updateCompanyMigration(companyMigration);
            }
        } catch (MigrationDAOException e) {
            log.warn("error [" + e.getMessage() + "] occured while auditing the status: " + getActivityStatus() + " for company: "
                    + companyMigration.toString());
        }
        log.info("company: " + companyMigration.toString() + ", migration status: " + getActivityStatus());
    }
}
