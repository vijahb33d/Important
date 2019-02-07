package com.telstra.olb.tegcbm.job.concurrency;

import java.util.List;

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.emory.mathcs.backport.java.util.concurrent.ConcurrentHashMap;

/**
 * Creates a task and manages the task. If the task identfied by identifier is already running, then 
 * a new task is not created, rather a null object is returned. The calling method should check for nulls, if using
 * this task manager to create tasks. Any tasks created by this manager is managed by this manager and is removed
 * from the list of alive tasks, once the task completes execution.
 */
public class TaskManager {
    
    private static Log log = LogFactory.getLog(TaskManager.class);
    
    /**
     * Map of alive tasks.
     */
    private ConcurrentHashMap executingTasks = new ConcurrentHashMap();
      
    /**
     * Creates a task that can executed in a thread.
     *
     * @param taskIdentifier taskIdentifier
     * @param taskInput task input
     * @param uow unit of work
     * @param taskCompleteListeners taskCompleteListeners
     * @return tprm task.
     */
    public Task create(String taskIdentifier, Object taskInput, Transformer uow, List taskCompleteListeners){
        if (isExecuting(taskIdentifier)) {
            log.info("Task: " + taskIdentifier + "; still processing..."); 
            // If the task identfied by identifier is already running, then a new task is not created, rather a null object is returned
            return null;
        } 
        Task task = new Task(taskIdentifier, uow, taskInput, taskCompleteListeners);
        task.setManager(this);
        if (log.isDebugEnabled()) { log.debug("Task: " + taskIdentifier + " created."); }
        executingTasks.putIfAbsent(taskIdentifier, task);
        return task;
    }
    
    /**
     * Removes the task from the list of executing tasks.
     * @param taskId completed task identifier.
     * @see com.telstra.olb.tegcbm.util.TaskCompleteListener#taskComplete(com.telstra.olb.tegcbm.util.TaskCompleteEvent)
     */
    public void taskCompleted(String taskId) {
        Task removed = (Task) executingTasks.remove(taskId);
        if (removed == null) { 
            log.warn("unable to remove or find Task with id: " + taskId); 
        }
        if (log.isDebugEnabled()) { log.debug("Task: " + taskId + " completed. Remaining tasks: " + executingTasks.size()); }
    }
    /**
     * Checks whether the task specified by the identifier is currently running.
     * @param taskIdentifier task identifier.
     * @return true if the task is in the active tasks list.
     */
    public boolean isExecuting(Object taskIdentifier) {
        return executingTasks.containsKey(taskIdentifier);
    }
    /**
     * Checks whether the created tasks has finished processing. 
     *
     * @return true if there are tasks currently executed.
     */
    public boolean containsExecutingTasks() {
        return !executingTasks.isEmpty();
    }
}
