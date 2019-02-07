/*
 * ActivityUOW.java
 * Created on 12/07/2007 by pavan.x.kuma
 */
package com.telstra.olb.tegcbm.job.core;

import org.apache.commons.collections.Transformer;

/**
 * ActivityUOW is an adapter pattern, that provide a wrapper to run by an executor service. It is used by the 
 * Runnable object that is created by the task manager as a visitor pattern to execute the activity. Since there is 
 * no value while executing the activity, the wrapper returns a null object.
 *
 */
class ActivityUOW implements Transformer {
    private Activity delegate;
    
    /**
     * 
     * @param delegate activity delegate
     */
    public ActivityUOW(Activity delegate) {
        this.delegate = delegate;
    }
    
    /**
     * Calls run on the delegate. A runtime exception is thrown if delegate is not set. Since there is 
     * no value while executing the activity, the wrapper returns a null object.
     * @param taskInput taskinput which is also activity input
     * @return null, as activities does not return a value.
     * @see org.apache.commons.collections.Transformer#transform(java.lang.Object)
     */
    public Object transform(Object taskInput) {
        try {
            if (delegate == null) {
                throw new RuntimeException("delegate not set");
            }
            delegate.execute((IContext)taskInput);
        } catch (ActivityException e) {
            throw new RuntimeException("error during migration activity: " + delegate.getName(), e);
        }
        return null;
    }
}
