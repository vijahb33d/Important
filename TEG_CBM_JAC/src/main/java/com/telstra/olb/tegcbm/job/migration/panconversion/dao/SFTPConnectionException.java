/*
 * PopulateActivityException.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.dao;


/**
 * Populate Data Exception.
 */
public class SFTPConnectionException extends Exception {
    
    /**
     * 
     */
    public SFTPConnectionException() {
        super();

    }
    /**
     * @param message exception message.
     */
    public SFTPConnectionException(String message) {
        super(message);

    }
    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public SFTPConnectionException(String message, Throwable cause) {
        super(message, cause);

    }
    /**
     * @param cause exception cause.
     */
    public SFTPConnectionException(Throwable cause) {
        super(cause);

    }
}
