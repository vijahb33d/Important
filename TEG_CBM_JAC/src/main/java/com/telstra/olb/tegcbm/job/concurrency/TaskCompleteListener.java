package com.telstra.olb.tegcbm.job.concurrency;

/**
 * Notifies the completion of the task along with its status.
 * 
 * @author pavan.x.kuma
 *
 */
public interface TaskCompleteListener {
	/**
	 * Notifies the observer that the task has been completed.
	 * @param event contains the status of the task.
	 */
	void taskComplete(TaskCompleteEvent event);
}
