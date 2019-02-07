/*
 * ConcurrentMigrationActivity.java
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.telstra.olb.tegcbm.job.concurrency.Task;
import com.telstra.olb.tegcbm.job.concurrency.TaskCompleteEvent;
import com.telstra.olb.tegcbm.job.concurrency.TaskCompleteListener;
import com.telstra.olb.tegcbm.job.concurrency.TaskManager;
import com.telstra.olb.tegcbm.job.concurrency.TaskStatus;

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 *  AsynchronousActivity is a decorator pattern implementation of an activity that executes an activity is a thread.
 *  Threads are created per activity input data unit and is executed by an executor service configured on this activity.
 **/
public class AsynchronousActivity extends DefaultActivity implements TaskCompleteListener {
    /**
     *  
     */
    private ExecutorService executorService;

    /**
     *  
     */
    private TaskManager taskManager;

    /**
     *  
     */
    private Activity activityDelegate;
    /**
     * 
     */
    private long sleepInterval = THREAD_SLEEP_INTERVAL;
    
    /**
     * 
     */
    private static final long THREAD_SLEEP_INTERVAL = 1000;

    /**
     * pre execute processing hook.
     * @param activityContext activity input.
     * @throws ActivityException is thrown by concrete implementations.
     */
    protected void preExecute(IContext activityContext) throws ActivityException {
        if ((taskManager == null) || (executorService == null)) {
            throw new ActivityException("invalid configuration for activity: " + getName());
        }
    }
    /**
     * Runs the activity configured as delegate in a thread. This activity does not support any activity listeners. 
     * The delegate configured with this activity is responsible for updating the activity status.
     * @param input data unit of activity input.
     * @throws ActivityException
     *             is thrown if the excutor service or task manager is not set.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doExecute(com.telstra.olb.tegcbm.migration.core.ActivityInput)
     */
    public void doExecute(Object input) throws ActivityException {
        List listeners = new ArrayList();
        listeners.add(this);
        String taskId = getName() + "::" + input.toString();
        Task task = taskManager.create(taskId, toContext(input), new ActivityUOW(activityDelegate), listeners);
        if (task != null) {
            if (log.isDebugEnabled()) { log.debug("executing task: " + taskId + " for activity: " + getName()); }
            executorService.execute(task);
        }
    }
    /**
     * post execute processing hook. contains implementation that wait for all 
     * concurrent tasks that were spawned to finish.
     * @param activityContext activity input.
     * @throws ActivityException can be thrown by overridden methods.
     */
    protected void postExecute(IContext activityContext) throws ActivityException {
        while (taskManager.containsExecutingTasks()){
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
   /**
    * Input is from command line args. process command line args.
    * 
    * @param args
    *            command line args.
    * @return data iterator.
    */
    protected Iterator processInputArgs(InputArgs args) {
        return activityDelegate.getData(args);
    }
    /**
     * Get the data for the activity from the delegate.
     * @return data iterator.
     * @see com.telstra.olb.tegcbm.job.core.Activity#doGetData(java.lang.Object)
     */
    protected Iterator doGetData() {
        return activityDelegate.getData(null);
    }
    /**
     * Listens for task complete event.
     * 
     * @param event
     *            task complete event.
     * @see com.telstra.olb.tegcbm.job.concurrency.TaskCompleteListener#taskComplete(com.telstra.olb.tegcbm.tasks.TaskCompleteEvent)
     */
    public void taskComplete(TaskCompleteEvent event) {
        if (event == null) {
            return;
        }
        Task task = event.getTask();
        String taskId = task.getTaskId();
        if (log.isDebugEnabled()) { log.debug("activity task: " + taskId + " completed with status: " + event.getStatus()); }
        if (event.getStatus() == TaskStatus.TASK_FAILURE) {
            log.error("===error stack trace===", event.getError());  
        }
    }

    /**
     * @return Returns the executorService.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * @param executorService
     *            The executorService to set.
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * @return Returns the migrationActivityDelegate.
     */
    public Activity getActivityDelegate() {
        return activityDelegate;
    }

    /**
     * @param migrationActivityDelegate
     *            The migrationActivityDelegate to set.
     */
    public void setActivityDelegate(Activity migrationActivityDelegate) {
        this.activityDelegate = migrationActivityDelegate;
    }

    /**
     * @return Returns the taskManager.
     */
    public TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * @param taskManager
     *            The taskManager to set.
     */
    public void setTaskManager(TaskManager taskManager) {
        this.taskManager = taskManager;
    }
    /**
     * @return Returns the sleepInterval.
     */
    public long getSleepInterval() {
        return sleepInterval;
    }
    /**
     * @param sleepInterval The sleepInterval to set.
     */
    public void setSleepInterval(long sleepInterval) {
        this.sleepInterval = sleepInterval;
    }
}
