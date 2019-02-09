/*
 * MigrationActivityProcessingListener.java
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.ActivityStatusEvent;
import com.telstra.olb.tegcbm.job.core.ActivityStatusListener;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyPreference;

/**
 * Default Activity status Listener adds an audit entry in the data layer
 * for the activity status configured. 
 */
public class DefaultActivityStatusListener implements ActivityStatusListener {
    protected transient Log log = LogFactory.getLog(getClass());
    
    private ActivityStatus activityStatus;

    private MigrationDAO migrationDao; 

    /**
     * @return Returns the migrationDao.
     */
    public MigrationDAO getMigrationDao() {
        return migrationDao;
    }
    /**
     * @param migrationDao
     *            The migrationDao to set.
     */
    public void setMigrationDao(MigrationDAO migrationDao) {
        this.migrationDao = migrationDao;
    }
    /**
     * @return Returns the activityStatus.
     */
    public ActivityStatus getActivityStatus() {
        return activityStatus;
    }
    /**
     * configures the status that this listener can handle. 
     *
     * @param status status.
     */
    public void setActivityStatus(ActivityStatus status) {
        this.activityStatus = status;
    }

    /**
     * Template method to handle the status of the current activity in context. Extended classes
     * implement <code>doHandleActivityStatus</code>.
     * 
     * @param event
     *            activity status event.
     * @see com.telstra.olb.tegcbm.job.core.ActivityStatusListener#handleActivityStatus(com.telstra.olb.tegcbm.job.core.ActivityStatusEvent)
     */
    public final void handleActivityStatus(ActivityStatusEvent event) {
        if (handlesActivityStatus(event.getStatus())) {
            if (log.isDebugEnabled()) { log.debug("handling status: " + event.getStatus() + "; activity: " + event.getContext().getName()); }
            doHandleActivityStatus(event.getContext(), event.getEventParams());
        } 
    }

    /**
     * Checks whether the activity status is the one that has been configured. If no activty status is configured,
     * then it returns false.
     * @param status
     *            activity status
     * @return true if handled.
     */
    protected boolean handlesActivityStatus(ActivityStatus status) {
        return this.activityStatus != null ? status == this.activityStatus : false;
    }

    /**
     * concrete implementation of handle method for activity status listener.
     * Added conditions for OLBTBUnmanagedCompanyPreference - CR3134
     * 
     * @param activity activity
     * @param eventParams event params.
     */
    protected void doHandleActivityStatus(Activity activity, Object eventParams) {
        	 OLBCompanyMigration companyMigration = null;
        	 String comments = null;
             if(eventParams instanceof OLBCompanyMigration){
             	companyMigration = (OLBCompanyMigration) eventParams;
             	comments = getComments(activity, companyMigration);
                audit(activity, companyMigration, comments);
             } else if(eventParams instanceof OLBTBUnmanagedCompanyPreference) {
             	OLBTBUnmanagedCompanyPreference pref = (OLBTBUnmanagedCompanyPreference)eventParams;
             	companyMigration = (OLBTBUnmanagedCompanyMigration)pref.getCompany();
             	comments = getComments(activity, companyMigration);
                auditTBU(activity, companyMigration, comments);
             } else {
                if (log.isDebugEnabled()) {
                    log.debug("status: " + activityStatus + " cannot be handled by " + getClass().getName() + ". Reason: Wrong or null event Params");
                }
             }
             
    }
    
    /**
     * adds an audit entry.
     * 
     * @param activity
     *            activity
     * @param companyMigration
     *            company
     * @param comments
     *            comments
     */
    protected void auditTBU(Activity activity, OLBCompanyMigration companyMigration, String comments) {
        try {
            if (migrationDao != null) {
                migrationDao.auditTBU(companyMigration.getCompanyCode(), activity.getName(), activityStatus.getName(), comments);
            }
        } catch (MigrationDAOException e) {
            log.warn("error [" + e.getMessage() + "] occured while auditing the status: " + activityStatus + " for activity: "
                    + activity.getName());
        }
    }
    

    /**
     * adds an audit entry.
     * 
     * @param activity
     *            activity
     * @param companyMigration
     *            company
     * @param comments
     *            comments
     */
    protected void audit(Activity activity, OLBCompanyMigration companyMigration, String comments) {
        try {
            if (migrationDao != null) {
                migrationDao.audit(companyMigration.getCompanyCode(), activity.getName(), activityStatus.getName(), comments);
            }
        } catch (MigrationDAOException e) {
            log.warn("error [" + e.getMessage() + "] occured while auditing the status: " + activityStatus + " for activity: "
                    + activity.getName());
        }
    }

    /**
     * gets the comment string for the activity status event.
     * 
     * @param activity
     *            activity
     * @param companyMigration
     *            company details.
     * @return comment string
     */
    private String getComments(Activity activity, OLBCompanyMigration companyMigration) {
        StringBuffer comments = new StringBuffer();
        comments.append(activity.getName()).append(": {");
        if (companyMigration != null) {
            comments.append("companyDetails=").append(companyMigration.toString());
        } else {
            comments.append("no params available");
        }
        comments.append("}");
        if (log.isDebugEnabled()) { log.debug(comments); }
        return comments.toString();
    }
    
}
