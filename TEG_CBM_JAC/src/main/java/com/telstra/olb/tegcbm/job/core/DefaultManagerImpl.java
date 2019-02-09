package com.telstra.olb.tegcbm.job.core;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.client.Manager;
import com.telstra.olb.tegcbm.client.ReturnStatus;

/**
 * Manager implementation that coordinates the Migration process. 
 *
 * @author daniel.fajerman
 */
public class DefaultManagerImpl implements Manager, ActivityStatusListener {
    private static Log log = LogFactory.getLog(DefaultManagerImpl.class);

    private List jobs;
    
    private boolean stopOnError = false;
    
    private String usage = "";

    /**
     * @param jobs List of jobs to execute.
     */
    public void setJobs(List jobs) {
        this.jobs = jobs;
    }
    /**
     * @return Returns the stopOnError.
     */
    private boolean isStopOnError() {
        return stopOnError;
    }
    /**
     * @param stopOnError The stopOnError to set.
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
    
    /**
     * Runs the migration process.
     * @param args migration arguments
     * @return return status
     */
    public final ReturnStatus execute(List args) {
        if(!checkArgs(args)) {
			return ReturnStatus.INVALID_ARGS;
		}
        ReturnStatus returnValue = ReturnStatus.SUCCESS;
        InputArgs migrationArgs = new InputArgs(args);
        Iterator iterator = jobs.iterator();
        while (iterator.hasNext()) {
            Activity job = (Activity) iterator.next();
            try {
                log.info("executing job: " + job.getName());
                job.execute(migrationArgs);
                log.info("completed job: " + job.getName());
            } catch (ActivityException e) {
                returnValue = ReturnStatus.FAILURE;
                log.error("error while executing job: " + job.getName(), e);
                if (isStopOnError()) {
                    break;
                }
            }
        }
        if (log.isDebugEnabled()) { log.debug("finished running #" + jobs.size() + " jobs. status: " + returnValue); }
        return returnValue;
    }

    /**
     * validates the list of arguments.
     * 
     * @param args
     *            list of arguments
     * @return true/false
     */
    protected boolean checkArgs(List args) {
        return ((jobs != null) && (!jobs.isEmpty()));
    }

    /**
     * Usage Message specific to the implementation.
     * @return String usage message
     */
    public String usage() {
        return usage;
    }
    
    /**
     * @param usage The usage to set.
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }
    /**
     * listens to the status of the job.
     * @param event job status
     * @see com.telstra.olb.tegcbm.job.core.ActivityStatusListener#handleActivityStatus(com.telstra.olb.tegcbm.job.core.ActivityStatusEvent)
     */
    public void handleActivityStatus(ActivityStatusEvent event) {
    }
}
