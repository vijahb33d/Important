package com.telstra.olb.tegcbm.job.concurrency;

/**
 * Event object which holds the state of a completed task. It is used for processing by
 * <code>TaskCompleteListener</code> implementations.
 * 
 * @author pavan.x.kuma
 */
public class TaskCompleteEvent {
	
    /**
	 * task.
	 */
	private Task task;
	/**
	 * status of the task.
	 */
	private TaskStatus status;
	/**
	 * 
	 */
	private Object taskParams;
	
	/**
	 * Task completeion event.
	 * @param task task 
	 * @param status successful status message
	 * @param taskParams task event parameters
	 */
	public TaskCompleteEvent(Task task, TaskStatus status, Object taskParams) {
		this.task = task;
		this.status = status;
		this.taskParams = taskParams;
	}

	/**
	 * @return the error if there are any, else returns null.
	 */
	public Throwable getError() {
		return status == TaskStatus.TASK_FAILURE ? (Throwable) taskParams : null;
	}

	/**
	 * @return the status
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * @return the task
	 */
	public Task getTask() {
		return task;
	}
	/**
	 * The task is considered to be an errored task if the TaskStatus is TASK_FAILURE.
	 * @return true if the task has erred.
	 */
	public boolean isErrorEvent() {
		return status == TaskStatus.TASK_FAILURE;
	}
}
