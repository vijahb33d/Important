package com.telstra.olb.tegcbm.job.core;

/**
 * Activity Status Event is used by ActivityStatusListener interface to
 * get information about which activity has notified the status event. This
 * object holds the information about
 * <li> context - Activity that published the event</li>
 * <li> status - status of the activity</li>
 * <li> eventParams - options event parameters</li>
 */
public final class ActivityStatusEvent {
    private Activity context;
    private ActivityStatus status;
    private Object eventParams;
    /**
     * 
     * @param context activity 
     * @param status status of the activity
     * @param eventParams event params.
     */
    public ActivityStatusEvent(Activity context, ActivityStatus status, Object eventParams) {
        this.context = context;
        this.status = status;
        this.eventParams = eventParams;
    }
    /**
     * @return Returns the context.
     */
    public Activity getContext() {
        return context;
    }
    /**
     * @param context The context to set.
     */
    public void setContext(Activity context) {
        this.context = context;
    }
    /**
     * @return Returns the status.
     */
    public ActivityStatus getStatus() {
        return status;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(ActivityStatus status) {
        this.status = status;
    }
    /**
     * @return Returns the eventParams.
     */
    public Object getEventParams() {
        return eventParams;
    }
    /**
     * @param eventParams The eventParams to set.
     */
    public void setEventParams(Object eventParams) {
        this.eventParams = eventParams;
    }
}
