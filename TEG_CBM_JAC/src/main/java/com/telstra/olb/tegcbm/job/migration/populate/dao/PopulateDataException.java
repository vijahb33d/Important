/*
 * PopulateActivityException.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;


/**
 * Populate Data Exception.
 */
public class PopulateDataException extends Exception {
    
    /**
     * 
     */
    public PopulateDataException() {
        super();

    }
    /**
     * @param message exception message.
     */
    public PopulateDataException(String message) {
        super(message);

    }
    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public PopulateDataException(String message, Throwable cause) {
        super(message, cause);

    }
    /**
     * @param cause exception cause.
     */
    public PopulateDataException(Throwable cause) {
        super(cause);

    }
}
