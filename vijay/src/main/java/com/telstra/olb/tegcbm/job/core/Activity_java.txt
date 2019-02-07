package com.telstra.olb.tegcbm.job.core;

import java.util.Iterator;

/**
 * Interface which represents a job activity. It abstracts away the data required for the Activity to an Iterator, 
 * and is chainable.
 * @author daniel.fajerman
 */
public interface Activity {
    
    /**
     *  
     * @return the name of the migration activity.
     */
    String getName();
    
    /**
     * Concrete implementations of activities implement this method
     * to handle a job activity. 
     *
     * @param activityContext input for the activty.
     * @throws ActivityException is thrown by this method.
     */
    void execute(IContext activityContext) throws ActivityException;
    
    /**
     * returns the next activity to be excecuted in the chain.
     *
     * @return next activity.
     */
    Activity getNextActivity();
    
    /**
     * 
     * Gets the data iterator for the activity.
     *
     * @param activityContext input args for the activity
     * @return data iterator.
     */
    Iterator getData(IContext activityContext);
}
