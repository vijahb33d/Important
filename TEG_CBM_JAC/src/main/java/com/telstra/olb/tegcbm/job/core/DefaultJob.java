/*
 * Job.java
 * Created on 18/07/2007 by pavan.x.kuma
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

import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;

/**
 * <p>
 * Default Job is a composite pattern that contains other activities to be executed. Moreover it provides
 * functionality to run a mutually execlusive activities in threads. The mutually exclusive activities are 
 * modelled as synchronous activities. The activities that are to be executed in a thread are configured by 
 * the property concurrentActivities.
 * </p> 
 * <p>
 * It is different to AsynchronousActivity. AsynchronousActivity runs a dataitem in a thread for an activity. 
 * If an asynchronous activity is configured, then a thread is created for that activity and that activity 
 * in turn creates thread per data item. In such cases running a asynchronous activity as a concurrent activity 
 * here is redudant and not advisable. Default job does not stop the developer configuring a asynchronous
 * activity as a concurrent activity.
 * </p>
 */
public class DefaultJob extends DefaultActivity implements CompositeActivity, TaskCompleteListener {
    /**
     * 
     */
    private static final long THREAD_SLEEP_INTERVAL = 1000;
    /**
     * List of child activities to be executed.
     */
    private List childActivities;
    
    /**
     * List of child activity names that can be run concurrently.
     */
    private List concurrentActivities;
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
    private long sleepInterval = THREAD_SLEEP_INTERVAL;
 
    /**
     * Executes the child activities configured. If the child activity 
     * has to be executed in a thread, a thread is spawned for the child 
     * activity. 
     * @param input activity input by default it is null.
     * @throws ActivityException
     *             is thrown if the excutor service or task manager is not set.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doExecute(com.telstra.olb.tegcbm.migration.core.ActivityInput)
     */
    public void doExecute(Object input) throws ActivityException {
        if (childActivities == null) {
            log.warn("no child activities configured for job: " + getName());
            return;
        }
        Iterator activities = childActivities.iterator();
        while (activities.hasNext()) {
            Activity activity = (Activity) activities.next();
            if ((concurrentActivities != null) && (concurrentActivities.contains(activity.getName()))) {
                runConcurrentActivity(activity, input);
            } else {
                activity.execute(toContext(input));
            }
        }
    }
    /**
     * Post execute processing hook. Contains implementation that wait for all 
     * concurrent activities to finish.
     * @param activityContext activity input.
     */
    protected void postExecute(IContext activityContext) {   
        while ((taskManager!= null) && (taskManager.containsExecutingTasks())) {
            if (log.isDebugEnabled()) { log.debug("there are pending concurrent activities still running..."); }
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
    /**
     * Runs the activity in a thread.  
     * 
     * @param activity activity.
     * @param input activity input
     * @throws ActivityException
     *             is thrown if the excutor service or task manager is not set.
     */
    private void runConcurrentActivity(Activity activity, Object input) throws ActivityException {
        if ((taskManager == null) || (executorService == null)) {
            throw new ActivityException("invalid configuration for activity: " + getName());
        }
        List listeners = new ArrayList();
        listeners.add(this);
        Task task = taskManager.create(activity.getName(), toContext(input), new ActivityUOW(activity), listeners);
        if (task != null) {
            if (log.isDebugEnabled()) { log.debug("executing activity: " + activity.getName() + " {" + task.getTaskId() + "}"); }
            executorService.execute(task);
        }
    }
    
    /**
     * default behaviour of Listener for activity thread complete event.
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
        if (log.isDebugEnabled()) { log.debug("job activity: " + taskId + " completed with status: " + event.getStatus()); }
    }
    
    
	/**
	 * Constructs an iterator with an null object.
	 * 
	 * @return an iterator with one null element.
	 */
	protected Iterator doGetData() {
		return new Iterator() {
			private boolean accessed = false;
            public boolean hasNext() {
                return !accessed;
            }

            public Object next() {
                accessed = true;
                return null;
            }

            public void remove() {
            }
		};
	}
	   
    /**
     * @return Returns the concurrentActivities.
     */
    public List getConcurrentActivities() {
        return concurrentActivities;
    }
    /**
     * @param concurrentActivities The concurrentActivities to set.
     */
    public void setConcurrentActivities(List concurrentActivities) {
        this.concurrentActivities = concurrentActivities;
    }
    /**
     * @return Returns the childActivities.
     */
    public List getChildActivities() {
        return childActivities;
    }
    /**
     * @param childActivities The childActivities to set.
     */
    public void setChildActivities(List childActivities) {
        this.childActivities = childActivities;
    }
    /**
     * @return Returns the executorService.
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
    /**
     * @param executorService The executorService to set.
     */
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
    /**
     * @return Returns the taskManager.
     */
    public TaskManager getTaskManager() {
        return taskManager;
    }
    /**
     * @param taskManager The taskManager to set.
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
