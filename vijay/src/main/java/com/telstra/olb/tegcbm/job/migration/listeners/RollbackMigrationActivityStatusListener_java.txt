/*
 * RollbackMigrationActivityStatusListener.java
 * Created on 17/08/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.listeners;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * When Rollback activity is executed and is succesfully completed, then the migration status 
 * of the company is set to rollback.
 */

public class RollbackMigrationActivityStatusListener extends MigrationActivityStatusListener {
    
    /**
     * sets the migration status of the company to roll back if the complete activity status event is thrown.
     * @param activity activity
     * @param eventParams event params.
     * @see com.telstra.olb.tegcbm.job.migration.listeners.DefaultActivityStatusListener#doHandleActivityStatus(com.telstra.olb.tegcbm.job.core.Activity, java.lang.Object)
     */
    protected void doHandleActivityStatus(Activity activity, Object eventParams) {
        if ((eventParams instanceof OLBCompanyMigration) && (getActivityStatus() == ActivityStatus.STATUS_COMPLETE)) {
            updateMigrationStatus((OLBCompanyMigration) eventParams, ActivityStatus.STATUS_ROLLBACK);
        } else {
            super.doHandleActivityStatus(activity, eventParams);
        }
    }
}
