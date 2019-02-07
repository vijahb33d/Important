/*
 * PopulateActivityException.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.dao;


/**
 * Populate Data Exception.
 */
public class CreditCardDataException extends Exception {
    
    /**
     * 
     */
    public CreditCardDataException() {
        super();

    }
    /**
     * @param message exception message.
     */
    public CreditCardDataException(String message) {
        super(message);

    }
    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public CreditCardDataException(String message, Throwable cause) {
        super(message, cause);

    }
    /**
     * @param cause exception cause.
     */
    public CreditCardDataException(Throwable cause) {
        super(cause);

    }
}
