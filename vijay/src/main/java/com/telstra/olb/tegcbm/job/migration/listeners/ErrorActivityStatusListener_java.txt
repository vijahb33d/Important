/*
 * ErrorActivityStatusListener.java
 * Created on 18/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.listeners;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyPreference;

/**
 * Listener that is responsible for auditing error status for an activity. If the stack trace output is
 * aslo enabled, it adds the stack trace to the comment column of the database. If the total comment length is
 * greater than the size of the database column, then the comments are truncated. By default the comment length
 * is defined as 255 bytes in length and stack trace logging is disabled.
 */
public class ErrorActivityStatusListener extends DefaultActivityStatusListener {
    private boolean enableStackTrace = false;

    private static final int DEFAULT_COMMENT_LENGTH = 4000;

    private int commentLength = DEFAULT_COMMENT_LENGTH;

    /**
     * @return Returns the commentLength.
     */
    public int getCommentLength() {
        return commentLength;
    }

    /**
     * @param commentLength
     *            The commentLength to set.
     */
    public void setCommentLength(int commentLength) {
        this.commentLength = commentLength;
    }

    /**
     * @return Returns the enableStackTrace.
     */
    public boolean isEnableStackTrace() {
        return enableStackTrace;
    }

    /**
     * @param enableStackTrace
     *            The enableStackTrace to set.
     */
    public void setEnableStackTrace(boolean enableStackTrace) {
        this.enableStackTrace = enableStackTrace;
    }

    /**
     * handles the error status for the activity.
     * Added conditions for OLBTBUnmanagedCompanyPreference - CR3134
     * 
     * @param activity
     *            activity
     * @param eventParams
     *            event params.
     * @see com.telstra.olb.tegcbm.job.migration.listeners.DefaultActivityStatusListener#doHandleActivityStatus(com.telstra.olb.tegcbm.job.core.ActivityStatus,
     *      com.telstra.olb.tegcbm.job.core.Activity, java.lang.Object)
     */
    protected void doHandleActivityStatus(Activity activity, Object eventParams) {
        if (eventParams instanceof Object[]) {
            Object[] params = (Object[]) eventParams;
            String comments = getComments(activity, (Throwable) params[1]);
            OLBCompanyMigration companyMigration = null;
            if(params[0] instanceof OLBCompanyMigration){
            	companyMigration = (OLBCompanyMigration) params[0];
            } else if(params[0] instanceof OLBTBUnmanagedCompanyPreference) {
            	OLBTBUnmanagedCompanyPreference pref = (OLBTBUnmanagedCompanyPreference)params[0];
            	companyMigration = (OLBCompanyMigration)pref.getCompany();
            }
            audit(activity, companyMigration, comments);
        } else {
            super.doHandleActivityStatus(activity, eventParams);
        }
    }

    /**
     * gets the comments string for the error status.
     * 
     * @param activity
     *            activity
     * @param error
     *            Exception.
     * @return comment string
     */
    private String getComments(Activity activity, Throwable error) {
        StringBuffer comments = new StringBuffer();
        comments.append(activity.getName()).append(": {");
        if (error != null) {
            Throwable t = ExceptionUtils.getRootCause(error);
            comments.append("errorDetails=").append(t != null ? t.getMessage() : error.getMessage());
            if (isEnableStackTrace()) {
                String stackTrace = ExceptionUtils.getFullStackTrace(error);
                comments.append(", ").append("stackTrace=").append(stackTrace);
            }
        } else {
            comments.append("no params available");
        }
        comments.append("}");  
        int length = (comments.toString().length() > getCommentLength()) ? getCommentLength() : comments.toString().length();
        return getCommentLength() != -1 ? comments.toString().substring(0, length) : comments.toString();
    }
}
