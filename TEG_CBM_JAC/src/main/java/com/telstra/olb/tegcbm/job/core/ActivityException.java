/*
 * ActivityException
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.core;

/**
 * Activity Exception is the default exception for any activity related
 * exceptons.
 */
public class ActivityException extends Exception {

    /**
     * 
     */
    public ActivityException() {
        super();

    }

    /**
     * @param message activity exception message
     */
    public ActivityException(String message) {
        super(message);

    }

    /**
     * @param message activity exception message
     * @param cause actual cause
     */
    public ActivityException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause actual cause
     */
    public ActivityException(Throwable cause) {
        super(cause);

    }

}
