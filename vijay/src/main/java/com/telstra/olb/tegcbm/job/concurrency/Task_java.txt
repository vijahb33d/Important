package com.telstra.olb.tegcbm.job.concurrency;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generic Task object. This Task encapsulates:
 * <ul>
 * <li>A task worker transformer
 * <li>A task manager
 * <li>Input data
 * <li>Task Complete listeners
 * <li>Result data
 * </ul>
 * @author pavan.x.kuma
 */
public class Task implements Runnable {
    protected transient Log log = LogFactory.getLog(getClass());
	
	/**
	 * Task Complete listeners.
	 */
	private List taskCompleteListeners;
	/**
	 * 
	 */
	private boolean running = false;
	/**
	 * 
	 */
    private Transformer worker;
    /**
     * 
     */
    private Object taskInput;
    /**
     * 
     */
    private String taskId;
    /**
     * 
     */
    private Object result;
    /**
     * 
     */
    private TaskManager manager;
	
	/**
	 * Creates a task.
	 * @param taskId task identifier.
	 * @param worker worker class that contains the execution details of the task.
	 * @param taskInput task input.
	 * @param taskCompleteListeners list of listeners that needs to be notified once the task is complete.
	 */
    Task(String taskId, Transformer worker, Object taskInput, List taskCompleteListeners) {
        this.taskId = taskId;
        this.taskCompleteListeners = taskCompleteListeners;
        this.worker = worker;
        this.taskInput = taskInput;
    }

    /**
     * 
     * @see java.lang.Runnable#run()
     */
    public final void run() {
        running = true;
        try {
            if (log.isDebugEnabled()) { log.debug("running task: " + taskId); }
            result = worker.transform(taskInput);
            notifyListeners(TaskStatus.TASK_SUCCESS, null);
        } catch (Throwable t) {
            notifyListeners(TaskStatus.TASK_FAILURE, t);
        } finally {
            running = false;
            if (manager != null) {
                if (log.isDebugEnabled()) { log.debug("removing task: " + taskId + " from manager."); }
                manager.taskCompleted(taskId);
            }
        }
    }
    /**
     * Notifies the task complete listeners of the status of the task.
     *
     * @param taskStatus task status
     * @param t if an error is thrown.
     */
    private void notifyListeners(TaskStatus taskStatus, Throwable t) {
        if ((taskCompleteListeners == null) || (taskCompleteListeners.isEmpty())) {
            log.info("no task complete listeners are available for " + taskId);
        }
        TaskCompleteEvent event = new TaskCompleteEvent(this, taskStatus, t);
        Iterator listeners = taskCompleteListeners.iterator();
		while (listeners.hasNext()) {
		    TaskCompleteListener listener = (TaskCompleteListener) listeners.next();
		    listener.taskComplete(event);
		}
    }
    
    /**
     * @return Returns the result.
     */
    public Object getResult() {
        return result;
    }
    /**
     * @return Returns the running.
     */
    public boolean isRunning() {
        return running;
    }
    /**
     * @return Returns the taskId.
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the taskInput
     */
    public Object getTaskInput() {
        return taskInput;
    }
    /**
     * @param manager The manager to set.
     */
    void setManager(TaskManager manager) {
        this.manager = manager;
    }
}
